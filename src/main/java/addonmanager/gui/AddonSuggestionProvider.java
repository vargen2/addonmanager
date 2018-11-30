package addonmanager.gui;

import addonmanager.app.App;
import addonmanager.app.CurseAddon;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import org.controlsfx.control.textfield.AutoCompletionBinding;

import java.util.*;

public class AddonSuggestionProvider implements Callback<AutoCompletionBinding.ISuggestionRequest, Collection<CurseAddon>> {

    private final List<CurseAddon> possibleSuggestions = new ArrayList<>();
    private final Object possibleSuggestionsLock = new Object();
    private ObservableList<CurseAddon> observedList;

    private Callback<CurseAddon, String> stringConverter;

    private final Comparator<CurseAddon> stringComparator = new Comparator<CurseAddon>() {
        @Override
        public int compare(CurseAddon o1, CurseAddon o2) {
            String o1str = stringConverter.call(o1);
            String o2str = stringConverter.call(o2);
            return o1str.compareTo(o2str);
        }
    };


    private AddonSuggestionProvider(Callback<CurseAddon, String> stringConverter) {
        this.stringConverter = stringConverter;

        // In case no stringConverter was provided, use the default strategy
        if (this.stringConverter == null) {
            this.stringConverter = new Callback<CurseAddon, String>() {
                @Override
                public String call(CurseAddon obj) {
                    return obj != null ? obj.toString() : ""; //$NON-NLS-1$
                }
            };
        }
    }

    /**
     * Add the given new possible suggestions to this  SuggestionProvider
     *
     * @param newPossible
     */
    public void addPossibleSuggestions(@SuppressWarnings("unchecked") CurseAddon... newPossible) {
        addPossibleSuggestions(Arrays.asList(newPossible));
    }

    /**
     * Add the given new possible suggestions to this  SuggestionProvider
     *
     * @param newPossible
     */
    public void addPossibleSuggestions(Collection<CurseAddon> newPossible) {
        synchronized (possibleSuggestionsLock) {
            possibleSuggestions.addAll(newPossible);
        }
    }

    /**
     * Remove all current possible suggestions
     */
    public void clearSuggestions() {
        synchronized (possibleSuggestionsLock) {
            possibleSuggestions.clear();
        }
    }

    public void setObservedList(ObservableList<CurseAddon> observedList) {
        this.observedList = observedList;
    }

    @Override
    public final Collection<CurseAddon> call(final AutoCompletionBinding.ISuggestionRequest request) {
        List<CurseAddon> suggestions = new ArrayList<>();
        if (!request.getUserText().isEmpty()) {
            synchronized (possibleSuggestionsLock) {
                for (CurseAddon possibleSuggestion : possibleSuggestions) {
                    if (isMatch(possibleSuggestion, request)) {
                        suggestions.add(possibleSuggestion);
                    }
                }
            }
            if (observedList != null && suggestions.size() <= 50) {
                observedList.setAll(suggestions);
                observedList.sort(new Comparator<CurseAddon>() {
                    @Override
                    public int compare(CurseAddon o1, CurseAddon o2) {

                        return Long.compare(o2.getDownloads(), o1.getDownloads());


                    }
                });

            } else {
                observedList.setAll(App.curseAddons);

            }
            suggestions.sort(getComparator());

        } else {
            if (observedList != null) {
                observedList.setAll(App.curseAddons);

            }
        }


        return suggestions;
    }


    private Comparator<CurseAddon> getComparator() {
        return stringComparator;
    }

    private boolean isMatch(CurseAddon suggestion, AutoCompletionBinding.ISuggestionRequest request) {
        String userTextLower = request.getUserText().toLowerCase();
        String suggestionStr = stringConverter.call(suggestion).toLowerCase();
        return suggestionStr.contains(userTextLower);
    }


    public static AddonSuggestionProvider create(Callback<CurseAddon, String> stringConverter, Collection<CurseAddon> possibleSuggestions) {
        AddonSuggestionProvider suggestionProvider = new AddonSuggestionProvider(stringConverter);
        suggestionProvider.addPossibleSuggestions(possibleSuggestions);
        return suggestionProvider;
    }


}
