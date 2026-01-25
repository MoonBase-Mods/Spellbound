package com.ombremoon.spellbound.common.magic.api;

public record SpellAnimation(String animation, Type type, boolean stationary) {

    public enum Type {
        CAST(true),
        CHANNEL(true);

        private final boolean stationary;

        Type(boolean stationary) {
            this.stationary = stationary;
        }

        public boolean isStationary() {
            return stationary;
        }
    }
}
