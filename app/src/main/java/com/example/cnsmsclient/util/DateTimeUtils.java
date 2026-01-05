package com.example.cnsmsclient.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for date and time formatting and manipulation.
 */
public class DateTimeUtils {

    private static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String DATE_FORMAT = "MMM dd, yyyy";
    private static final String TIME_FORMAT = "hh:mm a";
    private static final String DATETIME_FORMAT = "MMM dd, yyyy hh:mm a";
    private static final String SHORT_DATE_FORMAT = "MMM dd";

    /**
     * Format ISO date string to readable date
     */
    public static String formatDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty())
            return "";
        try {
            SimpleDateFormat isoFormatter = new SimpleDateFormat(ISO_FORMAT, Locale.getDefault());
            SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            Date date = isoFormatter.parse(isoDate);
            return dateFormatter.format(date);
        } catch (ParseException e) {
            return isoDate;
        }
    }

    /**
     * Format ISO date string to readable time
     */
    public static String formatTime(String isoDate) {
        if (isoDate == null || isoDate.isEmpty())
            return "";
        try {
            SimpleDateFormat isoFormatter = new SimpleDateFormat(ISO_FORMAT, Locale.getDefault());
            SimpleDateFormat timeFormatter = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
            Date date = isoFormatter.parse(isoDate);
            return timeFormatter.format(date);
        } catch (ParseException e) {
            return isoDate;
        }
    }

    /**
     * Format ISO date string to readable date and time
     */
    public static String formatDateTime(String isoDate) {
        if (isoDate == null || isoDate.isEmpty())
            return "";
        try {
            SimpleDateFormat isoFormatter = new SimpleDateFormat(ISO_FORMAT, Locale.getDefault());
            SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(DATETIME_FORMAT, Locale.getDefault());
            Date date = isoFormatter.parse(isoDate);
            return dateTimeFormatter.format(date);
        } catch (ParseException e) {
            return isoDate;
        }
    }

    /**
     * Get relative time string (e.g., "2 hours ago", "Yesterday")
     */
    public static String getRelativeTime(String isoDate) {
        if (isoDate == null || isoDate.isEmpty())
            return "";
        try {
            SimpleDateFormat isoFormatter = new SimpleDateFormat(ISO_FORMAT, Locale.getDefault());
            Date date = isoFormatter.parse(isoDate);
            long diffMillis = System.currentTimeMillis() - date.getTime();

            long minutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis);
            long hours = TimeUnit.MILLISECONDS.toHours(diffMillis);
            long days = TimeUnit.MILLISECONDS.toDays(diffMillis);

            if (minutes < 1) {
                return "Just now";
            } else if (minutes < 60) {
                return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
            } else if (hours < 24) {
                return hours + (hours == 1 ? " hour ago" : " hours ago");
            } else if (days == 1) {
                return "Yesterday";
            } else if (days < 7) {
                return days + " days ago";
            } else if (days < 30) {
                long weeks = days / 7;
                return weeks + (weeks == 1 ? " week ago" : " weeks ago");
            } else if (days < 365) {
                long months = days / 30;
                return months + (months == 1 ? " month ago" : " months ago");
            } else {
                SimpleDateFormat dateFormatter = new SimpleDateFormat(SHORT_DATE_FORMAT, Locale.getDefault());
                return dateFormatter.format(date);
            }
        } catch (ParseException e) {
            return isoDate;
        }
    }

    /**
     * Check if date is today
     */
    public static boolean isToday(String isoDate) {
        if (isoDate == null || isoDate.isEmpty())
            return false;
        try {
            SimpleDateFormat isoFormatter = new SimpleDateFormat(ISO_FORMAT, Locale.getDefault());
            Date date = isoFormatter.parse(isoDate);
            Calendar today = Calendar.getInstance();
            Calendar dateCalendar = Calendar.getInstance();
            dateCalendar.setTime(date);

            return today.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR)
                    && today.get(Calendar.DAY_OF_YEAR) == dateCalendar.get(Calendar.DAY_OF_YEAR);
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Get current ISO formatted timestamp
     */
    public static String getCurrentISOTimestamp() {
        SimpleDateFormat isoFormatter = new SimpleDateFormat(ISO_FORMAT, Locale.getDefault());
        return isoFormatter.format(new Date());
    }

    /**
     * Format duration in milliseconds to readable string
     */
    public static String formatDuration(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;

        if (hours > 0) {
            return String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
    }

    /**
     * Parse date from various formats
     */
    public static Date parseDate(String dateString) {
        String[] formats = {
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd",
                "dd/MM/yyyy",
                "MM/dd/yyyy"
        };

        for (String format : formats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
                return sdf.parse(dateString);
            } catch (ParseException ignored) {
            }
        }
        return null;
    }
}
