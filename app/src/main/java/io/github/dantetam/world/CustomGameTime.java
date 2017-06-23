package io.github.dantetam.world;

/**
 * Created by Dante on 6/22/2017.
 */
public class CustomGameTime {

    public int startYear;
    public int year;
    public int season; //0-3 inclusive: winter, spring, summer, fall
    public int day; //0-29 inclusive
    public int hour; //0-23 inclusive, standard 24 hour time

    public static final int NIGHT_LIGHT = 0;
    public static final int TWILIGHT_LIGHT = 5;
    public static final int NOON_LIGHT = 10;

    public CustomGameTime(int startYear) {
        this.startYear = startYear;
        this.year = startYear;
        season = 0;
        day = 0;
        hour = 0;
    }

    /*public void advanceHour() {
        advanceHours(1);
    }*/
    public void advanceHour() {
        hour++;
        if (hour >= 24) {
            hour %= 24;
            day++;
            if (day >= 30) {
                day %= 30;
                season++;
                if (season >= 4) {
                    season %= 4;
                    year++;
                }
            }
        }
    }

    //It's close enough to how actual light works. Note I do not actually think sunshine is a saw function.
    public int getLightLevel(int hour) {
        int distSummer = Math.abs(2 - season);
        int summerRise = 6, summerSet = 20;
        float offsetedRise = summerRise + distSummer;
        float offsetedSet = summerSet - 2 * distSummer;
        if (hour >= offsetedRise && hour <= offsetedSet) {
            float noon = (offsetedRise + offsetedSet) / 2;
            float distNoon = Math.abs(hour - noon);
            float noonTwilight = noon - offsetedRise;
            float percent = distNoon / noonTwilight;
            return (int) (percent * TWILIGHT_LIGHT + (1 - percent) * NOON_LIGHT);
        }
        else {
            float midnight = (offsetedRise + offsetedSet) / 2;
            midnight %= 24;
            float distMidnight = Math.abs(hour - midnight);
            float noonMidnight = midnight - offsetedRise;
            float percent = distMidnight / noonMidnight;
            return (int) (percent * TWILIGHT_LIGHT + (1 - percent) * NIGHT_LIGHT);
        }
    }

    public String getSeasonName() {
        if (season == 0) return "Winter";
        else if (season == 1) return "Spring";
        else if (season == 2) return "Summer";
        else return "Fall";
    }

}
