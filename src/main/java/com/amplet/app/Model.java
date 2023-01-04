package com.amplet.app;

import java.util.ArrayList;

public class Model implements Observed {
    ArrayList<Pile> allpiles;
    ArrayList<Carte> allcartes;
    ArrayList<Observer> observers;
    Context ctx;

    public Model() {

        allpiles = new ArrayList<Pile>();
        allcartes = new ArrayList<Carte>();
        observers = new ArrayList<Observer>();
        ctx = new Context();

    }

    public void addObserver(Observer o) {
        observers.add(o);
    }

    public void notifyAllObservers() {

    }

    public void mangemoilepoiro() {
        System.out.println("mangemoilepoiro");
    }

}
