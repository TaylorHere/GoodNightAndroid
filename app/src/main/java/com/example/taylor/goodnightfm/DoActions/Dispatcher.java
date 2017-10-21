package com.example.taylor.goodnightfm.DoActions;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taylor on 2017/10/8.
 */

public class Dispatcher {
        private static Dispatcher instance;
        private List<Store> stores = new ArrayList<>();

        public static Dispatcher get() {
            if (instance == null) {
                instance = new Dispatcher();
            }
            return instance;
        }

        Dispatcher() {}

        public void register(Store store) {
            if (stores.contains(store)){
                return;
            }
            stores.add(store);
        }

        public void unregister(Store store) {
            stores.remove(store);
        }

        public void dispatch(Action action) {
            post(action);
        }

        private void post(Action action) {
            int size = stores.size();
            for (int i = 0; i < size; i++) {
                Store store = stores.get(i);
                Log.v("action","dispatch action 【"+action.getType()+"】to 【"+store.getType()+"】store");
                store.onAction(action);
            }

        }
}
