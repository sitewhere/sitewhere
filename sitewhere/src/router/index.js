import Vue from 'vue'
import Router from 'vue-router'
import Vuetify from 'vuetify'
import Vue2Leaflet from 'vue2-leaflet'
import VueMoment from 'vue-moment'
// import VeeValidate from 'vee-validate'

import Login from '@/components/Login'
import TenantManager from '@/components/tenants/TenantManager'
import AdminApplication from '@/components/AdminApplication'
import Server from '@/components/server/Server'
import SitesList from '@/components/sites/SitesList'
import SiteDetail from '@/components/sites/SiteDetail'

Vue.use(Router)
Vue.use(Vuetify)
Vue.use(VueMoment)
// Vue.use(VeeValidate)

Vue.component('v-map', Vue2Leaflet.Map)
Vue.component('v-tilelayer', Vue2Leaflet.TileLayer)
Vue.component('v-marker', Vue2Leaflet.Marker)

// BEGIN HACK to get around problems with Leaflet issues.
import L from 'leaflet'

delete L.Icon.Default.prototype._getIconUrl

L.Icon.Default.mergeOptions({
  iconRetinaUrl: require('leaflet/dist/images/marker-icon-2x.png'),
  iconUrl: require('leaflet/dist/images/marker-icon.png'),
  shadowUrl: require('leaflet/dist/images/marker-shadow.png')
})

customize(L.Draw.Polyline.prototype)
customize(L.Edit.PolyVerticesEdit.prototype)

function customize (prototype) {
  var options = prototype.options
  options.icon.options.iconSize = new L.Point(10, 10)
  options.touchIcon = options.icon
}
// END HACK.

export default new Router({
  // mode: 'history',
  routes: [
    {
      path: '/',
      component: Login
    }, {
      path: '/tenants',
      component: TenantManager
    }, {
      path: '/admin/:tenantId',
      component: AdminApplication,
      children: [
        {
          path: 'server',
          component: Server
        }, {
          path: 'sites',
          component: SitesList
        }, {
          path: 'sites/:token',
          component: SiteDetail
        }, {
          path: 'devices',
          component: Server
        }, {
          path: 'specifications',
          component: Server
        }, {
          path: 'devicegroups',
          component: Server
        }, {
          path: 'assets',
          component: Server
        }, {
          path: 'batch',
          component: Server
        }, {
          path: 'schedules',
          component: Server
        }
      ]
    }
  ]
})
