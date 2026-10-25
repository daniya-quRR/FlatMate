
package com.kz.flatmate;

import com.kz.flatmate.model.User;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CompatibilityService {

    public record Comparison(
        String label,
        String yourAnswer,
        String roommateAnswer,
        boolean matches
    ) {}

    public record Result(
        int percentage,
        int comparedCount,
        List<Comparison> matches,
        List<Comparison> differences
    ) {}

    public Result compare(User current, User roommate) {
        List<Comparison> matches = new ArrayList<>();
        List<Comparison> differences = new ArrayList<>();

        check("Чистота", current.getCleanliness(),
              roommate.getCleanliness(), matches, differences);

        check("Режим сна", current.getSleepMode(),
              roommate.getSleepMode(), matches, differences);

        check("Уровень шума", current.getNoiseLevel(),
              roommate.getNoiseLevel(), matches, differences);

        check("Гости", current.getGuests(),
              roommate.getGuests(), matches, differences);

        check("Курение", current.getSmoking(),
              roommate.getSmoking(), matches, differences);

        check("Животные", current.getPets(),
              roommate.getPets(), matches, differences);

        check("Общительность", current.getSociability(),
              roommate.getSociability(), matches, differences);

        int total = matches.size() + differences.size();

        int percentage = total == 0 ? 0 :
            (int) Math.round(matches.size() * 100.0 / total);

        return new Result(
            percentage, total, matches, differences
        );
    }

    private void check(
        String label,
        String yours,
        String theirs,
        List<Comparison> matches,
        List<Comparison> differences
    ) {
        if (yours == null || theirs == null ||
            yours.isBlank() || theirs.isBlank()) {
            return;
        }

        boolean same = Objects.equals(
            yours.trim().toLowerCase(),
            theirs.trim().toLowerCase()
        );

        Comparison comparison =
            new Comparison(label, yours, theirs, same);

        if (same) {
            matches.add(comparison);
        } else {
            differences.add(comparison);
        }
    }
}
