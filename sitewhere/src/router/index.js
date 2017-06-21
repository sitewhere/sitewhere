import Vue from 'vue'
import Router from 'vue-router'
import Vuetify from 'vuetify'
import Login from '@/components/Login'
import TenantManager from '@/components/tenants/TenantManager'
import AdminApplication from '@/components/AdminApplication'
import Server from '@/components/server/Server'
import SitesList from '@/components/sites/SitesList'

Vue.use(Router)
Vue.use(Vuetify)

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
