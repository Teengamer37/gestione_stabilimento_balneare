package com.example.s_balneare.domain.beach;

/// Definisce i servizi offerti da una spiaggia
public record BeachServices(
        boolean bathrooms,
        boolean showers,
        boolean pool,
        boolean bar,
        boolean restaurant,
        boolean wifi,
        boolean volleyballField
) {
    //Factory statica per inizializzazione di BeachServices
    public static BeachServices none() {
        return new BeachServices(false, false, false, false, false, false, false);
    }

    //pattern Builder per creazione veloce dell'oggetto
    public static Builder builder() {
        return new Builder();
    }
    public static Builder builder(BeachServices services) {
        return new Builder(services);
    }

    //metodi singoli per fare update all'oggetto (metodi wither)
    public BeachServices withBathrooms(boolean bathrooms) {
        return new BeachServices(bathrooms, showers, pool, bar, restaurant, wifi, volleyballField);
    }
    public BeachServices withShowers(boolean showers) {
        return new BeachServices(bathrooms, showers, pool, bar, restaurant, wifi, volleyballField);
    }
    public BeachServices withPool(boolean pool) {
        return new BeachServices(bathrooms, showers, pool, bar, restaurant, wifi, volleyballField);
    }
    public BeachServices withBar(boolean bar) {
        return new BeachServices(bathrooms, showers, pool, bar, restaurant, wifi, volleyballField);
    }
    public BeachServices withRestaurant(boolean restaurant) {
        return new BeachServices(bathrooms, showers, pool, bar, restaurant, wifi, volleyballField);
    }
    public BeachServices withWifi(boolean wifi) {
        return new BeachServices(bathrooms, showers, pool, bar, restaurant, wifi, volleyballField);
    }
    public BeachServices withVolleyballField(boolean volleyballField) {
        return new BeachServices(bathrooms, showers, pool, bar, restaurant, wifi, volleyballField);
    }

    //pattern Builder per creazione BeachServices facilitata
    public static class Builder {
        private boolean bathrooms = false;
        private boolean showers = false;
        private boolean pool = false;
        private boolean bar = false;
        private boolean restaurant = false;
        private boolean wifi = false;
        private boolean volleyballField = false;

        public Builder() {
        }

        //costruttore copia
        public Builder(BeachServices original) {
            this.bathrooms = original.bathrooms;
            this.showers = original.showers;
            this.pool = original.pool;
            this.bar = original.bar;
            this.restaurant = original.restaurant;
            this.wifi = original.wifi;
            this.volleyballField = original.volleyballField;
        }

        public Builder bathrooms(boolean val) {
            bathrooms = val;
            return this;
        }

        public Builder showers(boolean val) {
            showers = val;
            return this;
        }

        public Builder pool(boolean val) {
            pool = val;
            return this;
        }

        public Builder bar(boolean val) {
            bar = val;
            return this;
        }

        public Builder restaurant(boolean val) {
            restaurant = val;
            return this;
        }

        public Builder wifi(boolean val) {
            wifi = val;
            return this;
        }

        public Builder volleyballField(boolean val) {
            volleyballField = val;
            return this;
        }

        public BeachServices build() {
            return new BeachServices(bathrooms, showers, pool, bar, restaurant, wifi, volleyballField);
        }
    }
}