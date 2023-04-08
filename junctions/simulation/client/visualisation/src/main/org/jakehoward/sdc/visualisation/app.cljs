(ns org.jakehoward.sdc.visualisation.app
  (:require [reagent.core :as r]
            [reagent.dom :as rd]))

(def example-carte
  {:universe {:x-min 0 :x-max 600 :y-min 0 :y-max 400}
   :roads [{:name "A1"
            :points [{:x 300 :y 0} {:x 300 :y 400}]}
           {:name "A2"
            :points [{:x 0 :y 200} {:x 600 :y 200}]}]
   :junctions [{:roads #{"A1" "A2"}}]})

(defn simulation->svg [{:keys [carte]}]
  (let [x-max (get-in carte [:universe :x-max])
        y-max (get-in carte [:universe :y-max])]
    [:div {:className "scene"}
     [:svg {:viewBox (str "0 0 " x-max " " y-max) :width x-max :height y-max}
      [:g
       [:rect
        {:width 10
         :height 10
         :color "red"
         :x 100
         :y 50}]]]]))


(defn visualisation [data]
  [:div
   [:p (str "Apparently, data: " (:data data))]
   (simulation->svg {:carte example-carte})])

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
