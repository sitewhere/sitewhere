import Vue from 'vue'
import Router from 'vue-router'
import Vuetify from 'vuetify'
import Vue2Leaflet from 'vue2-leaflet'
// import VeeValidate from 'vee-validate'

import Login from '@/components/Login'
import TenantManager from '@/components/tenants/TenantManager'
import AdminApplication from '@/components/AdminApplication'
import Server from '@/components/server/Server'
import SitesList from '@/components/sites/SitesList'

Vue.use(Router)
Vue.use(Vuetify)
// Vue.use(VeeValidate)

Vue.component('v-map', Vue2Leaflet.Map)
Vue.component('v-tilelayer', Vue2Leaflet.TileLayer)
Vue.component('v-marker', Vue2Leaflet.Marker)

// BEGIN HACK to get around problems with Leaflet relative icon URLs.
import L from 'leaflet'

delete L.Icon.Default.prototype._getIconUrl

L.Icon.Default.mergeOptions({
  iconRetinaUrl: require('leaflet/dist/images/marker-icon-2x.png'),
  iconUrl: require('leaflet/dist/images/marker-icon.png'),
  shadowUrl: require('leaflet/dist/images/marker-shadow.png')
})
// END HACK.

export default new Router({
  routes: [
    {
      path: '/',
      component: Login
    }, {
      path: '/tenants',
      component: TenantManager
    }, {
      path: '/admin',
      component: AdminApplication,
      children: [
        {
          path: 'server',
          component: Server
        }, {
          path: 'sites',
          component: SitesList
        }, {
          path: 'devices',
          component: SitesList
        }, {
          path: 'specifications',
          component: SitesList
        }, {
          path: 'devicegroups',
          component: SitesList
        }, {
          path: 'assets',
          component: SitesList
        }, {
          path: 'batch',
          component: SitesList
        }, {
          path: 'schedules',
          component: SitesList
        }
      ]
    }
  ]
})
