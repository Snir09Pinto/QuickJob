package com.example.project.managers;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * A class for handling Date and Time.
 */
public class DateAndTimeManager
{
    /**
     * @param milliSeconds - the timeline point.
     * @return a date format string.
     */
    public static String getDateString(long milliSeconds)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ConstantsManager.DATE_FORMAT);
        return simpleDateFormat.format(milliSeconds);
    }

    /**
     *
     * @param milliSeconds - the timeline point.
     * @return a time format string.
     *
     */
    public static String getTimeString(long milliSeconds)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ConstantsManager.TIME_FORMAT);
        return simpleDateFormat.format(milliSeconds);
    }

    /**
     * @param milliSeconds - the timeline point.
     * @return a time format string detailed so it can describe days and lower (time and date).
     */
    public static String getDetailedTimeString(long milliSeconds)
    {
        long days = TimeUnit.MILLISECONDS.toDays(milliSeconds);
        milliSeconds -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(milliSeconds);
        milliSeconds -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliSeconds);
        milliSeconds -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliSeconds);

        return String.format(ConstantsManager.DETAILED_TIME_FORMAT, days, hours, minutes, seconds);
    }

    /**
     * @param milliSeconds - the timeline point.
     * @return a regular time-date format string.
     */
    public static String getDateAndTimeString(long milliSeconds)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ConstantsManager.DATE_AND_TIME_FORMAT);
        return simpleDateFormat.format(milliSeconds);
    }
}
