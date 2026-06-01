package com.revature.userservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_preferences")
public class UserPreferences {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private Boolean notifications = true;

    private Boolean emailUpdates = true;

    private Boolean smsUpdates = true;

    private Boolean orderUpdates = true;

    private Boolean promotionalUpdates = false;

    @Column(length = 5)
    private String language = "en";

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Theme theme = Theme.LIGHT;

    public UserPreferences() {
    }

    public UserPreferences(Long id, User user, Boolean notifications, Boolean emailUpdates, Boolean smsUpdates,
            Boolean orderUpdates, Boolean promotionalUpdates, String language, Theme theme) {
        this.id = id;
        this.user = user;
        this.notifications = notifications;
        this.emailUpdates = emailUpdates;
        this.smsUpdates = smsUpdates;
        this.orderUpdates = orderUpdates;
        this.promotionalUpdates = promotionalUpdates;
        this.language = language;
        this.theme = theme;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getNotifications() {
        return notifications;
    }

    public void setNotifications(Boolean notifications) {
        this.notifications = notifications;
    }

    public Boolean getEmailUpdates() {
        return emailUpdates;
    }

    public void setEmailUpdates(Boolean emailUpdates) {
        this.emailUpdates = emailUpdates;
    }

    public Boolean getSmsUpdates() {
        return smsUpdates;
    }

    public void setSmsUpdates(Boolean smsUpdates) {
        this.smsUpdates = smsUpdates;
    }

    public Boolean getOrderUpdates() {
        return orderUpdates;
    }

    public void setOrderUpdates(Boolean orderUpdates) {
        this.orderUpdates = orderUpdates;
    }

    public Boolean getPromotionalUpdates() {
        return promotionalUpdates;
    }

    public void setPromotionalUpdates(Boolean promotionalUpdates) {
        this.promotionalUpdates = promotionalUpdates;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    // Builder
    public static UserPreferencesBuilder builder() {
        return new UserPreferencesBuilder();
    }

    public static class UserPreferencesBuilder {
        private Long id;
        private User user;
        private Boolean notifications = true;
        private Boolean emailUpdates = true;
        private Boolean smsUpdates = true;
        private Boolean orderUpdates = true;
        private Boolean promotionalUpdates = false;
        private String language = "en";
        private Theme theme = Theme.LIGHT;

        UserPreferencesBuilder() {
        }

        public UserPreferencesBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserPreferencesBuilder user(User user) {
            this.user = user;
            return this;
        }

        public UserPreferencesBuilder notifications(Boolean notifications) {
            this.notifications = notifications;
            return this;
        }

        public UserPreferencesBuilder emailUpdates(Boolean emailUpdates) {
            this.emailUpdates = emailUpdates;
            return this;
        }

        public UserPreferencesBuilder smsUpdates(Boolean smsUpdates) {
            this.smsUpdates = smsUpdates;
            return this;
        }

        public UserPreferencesBuilder orderUpdates(Boolean orderUpdates) {
            this.orderUpdates = orderUpdates;
            return this;
        }

        public UserPreferencesBuilder promotionalUpdates(Boolean promotionalUpdates) {
            this.promotionalUpdates = promotionalUpdates;
            return this;
        }

        public UserPreferencesBuilder language(String language) {
            this.language = language;
            return this;
        }

        public UserPreferencesBuilder theme(Theme theme) {
            this.theme = theme;
            return this;
        }

        public UserPreferences build() {
            return new UserPreferences(id, user, notifications, emailUpdates, smsUpdates, orderUpdates,
                    promotionalUpdates, language, theme);
        }
    }

    public enum Theme {
        LIGHT, DARK, SYSTEM
    }
}
