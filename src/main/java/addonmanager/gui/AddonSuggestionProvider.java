package addonmanager.gui;

import javafx.util.Callback;
import org.controlsfx.control.textfield.AutoCompletionBinding;

import java.util.*;

public class AddonSuggestionProvider<T> implements Callback<AutoCompletionBinding.ISuggestionRequest, Collection<T>> {

    private final List<T> possibleSuggestions = new ArrayList<>();
    private final Object possibleSuggestionsLock = new Object();
    private Set<T> observedSet;

    private Callback<T, String> stringConverter;

    private final Comparator<T> stringComparator = new Comparator<T>() {
        @Override
        public int compare(T o1, T o2) {
            String o1str = stringConverter.call(o1);
            String o2str = stringConverter.call(o2);
            return o1str.compareTo(o2str);
        }
    };


    private AddonSuggestionProvider(Callback<T, String> stringConverter) {
        this.stringConverter = stringConverter;

        // In case no stringConverter was provided, use the default strategy
        if (this.stringConverter == null) {
            this.stringConverter = new Callback<T, String>() {
                @Override
                public String call(T obj) {
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
    public void addPossibleSuggestions(@SuppressWarnings("unchecked") T... newPossible) {
        addPossibleSuggestions(Arrays.asList(newPossible));
    }

    /**
     * Add the given new possible suggestions to this  SuggestionProvider
     *
     * @param newPossible
     */
    public void addPossibleSuggestions(Collection<T> newPossible) {
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

    public void setObservedSet(Set<T> observedSet) {
        this.observedSet = observedSet;
    }

    @Override
    public final Collection<T> call(final AutoCompletionBinding.ISuggestionRequest request) {
        List<T> suggestions = new ArrayList<>();
        if (!request.getUserText().isEmpty()) {
            synchronized (possibleSuggestionsLock) {
                for (T possibleSuggestion : possibleSuggestions) {
                    if (isMatch(possibleSuggestion, request)) {
                        suggestions.add(possibleSuggestion);
                    }
                }
            }
            suggestions.sort(getComparator());
            if (observedSet != null && suggestions.size() <= 10) {
                observedSet.clear();
                observedSet.addAll(suggestions);
            }
        }


        return suggestions;
    }


    private Comparator<T> getComparator() {
        return stringComparator;
    }

    private boolean isMatch(T suggestion, AutoCompletionBinding.ISuggestionRequest request) {
        String userTextLower = request.getUserText().toLowerCase();
        String suggestionStr = stringConverter.call(suggestion).toLowerCase();
        return suggestionStr.contains(userTextLower);
    }


    public static <T> AddonSuggestionProvider<T> create(Callback<T, String> stringConverter, Collection<T> possibleSuggestions) {
        AddonSuggestionProvider<T> suggestionProvider = new AddonSuggestionProvider<>(stringConverter);
        suggestionProvider.addPossibleSuggestions(possibleSuggestions);
        return suggestionProvider;
    }


}
