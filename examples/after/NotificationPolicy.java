package examples.after;

/**
 * Replaces the boolean flags `sendEmail` + `sendSms`.
 * Each constant names the intent — no more positional booleans.
 */
public enum NotificationPolicy {
    NONE,
    EMAIL_ONLY,
    SMS_ONLY,
    EMAIL_AND_SMS;

    public boolean wantsEmail() { return this == EMAIL_ONLY || this == EMAIL_AND_SMS; }
    public boolean wantsSms()   { return this == SMS_ONLY   || this == EMAIL_AND_SMS; }
}
