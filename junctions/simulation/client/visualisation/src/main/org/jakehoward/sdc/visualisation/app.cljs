(ns org.jakehoward.sdc.visualisation.app
  (:require [reagent.core :as r]
            [reagent.dom :as rd]))


(defn visualisation [data]
  [:div
   [:p (str "Apparently, data: " (:data data))]])

(defn home-page []
  [:div
   [:h1 {:className "title"} "Four way junction"]
   [:div {:className "app-container"}
    [:div {:className "simulation"}
     [visualisation {:data :rocks}]]
    ]])

(defn init []
  (println "I'm a work in progress...")
  (rd/render [home-page] (.getElementById js/document "root")))
