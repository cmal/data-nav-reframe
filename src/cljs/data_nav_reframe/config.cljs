(ns data-nav-reframe.config)

(def debug?
  ^boolean js/goog.DEBUG)

(def icons
  [
   {:id "zmdi-home"    :label [:i {:class "zmdi zmdi-home"}]}
   {:id "zmdi-account" :label [:i {:class "zmdi zmdi-account"}]}
   {:id "zmdi-info"    :label [:i {:class "zmdi zmdi-info"}]}
   ;; {:id "zmdi-plus"    :label [:i {:class "zmdi zmdi-plus"}]}
   ;; {:id "zmdi-delete"  :label [:i {:class "zmdi zmdi-delete"}]}
   ;; {:id "zmdi-undo"    :label [:i {:class "zmdi zmdi-undo"}]}
   ]
  )

(def home-choices
  [
   {:id "PE" :label "PE" :group "fundamental"}
   {:id "PB" :label "PB" :group "fundamental"}
   {:id "k" :label "k线" :group "price"}
   {:id "md5" :label "MD5" :group "price"}
   {:id "md20" :label "MD20" :group "price"}
   {:id "md60" :label "MD60" :group "price"}
   {:id "egr" :label "净利增速" :group "Growth Rate"}
   {:id "rgr" :label "营收增速" :group "Growth Rate"}
   ])
