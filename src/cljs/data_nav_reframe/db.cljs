(ns data-nav-reframe.db)

(def default-db
  {
   :active-panel :main-panel
   :left-bar-active 0
   :input-text ""
   :show-panel-child (sorted-map-by >)
   :test "test"
   :circles [{:name "circle 1"
              :x 10
              :y 10
              :r 10
              :color "black"},
             {:name "circle 2"
              :x 35
              :y 35
              :r 15
              :color "red"},
             {:name "circle 3"
              :x 70
              :y 70
              :r 30
              :color "blue"}]
   })
