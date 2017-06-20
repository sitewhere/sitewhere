import Vue from 'vue'
import Router from 'vue-router'
import Vuetify from 'vuetify'
import Login from '@/components/Login'
import AdminApplication from '@/components/AdminApplication'

Vue.use(Router)
Vue.use(Vuetify)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Login',
      component: Login
    }, {
      path: '/admin',
      name: 'AdminApplication',
      component: AdminApplication
    }
  ]
})
