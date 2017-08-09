import Vue from 'vue'
import Router from 'vue-router'
import Vuetify from 'vuetify'
import Vue2Leaflet from 'vue2-leaflet'
import VueMoment from 'vue-moment'
import VueClipboards from 'vue-clipboards'
import VueHighlightJS from 'vue-highlightjs'
import VueFlatPickr from 'vue-flatpickr-component'
import Tree from 'element-ui'
// import VeeValidate from 'vee-validate'

import Login from '@/components/Login'
import SystemAdministration from '@/components/SystemAdministration'
import TenantsList from '@/components/tenants/TenantsList'
import TenantDetail from '@/components/tenants/TenantDetail'
import UsersList from '@/components/users/UsersList'
import TenantAdministration from '@/components/tenants/TenantAdministration'
import Server from '@/components/server/Server'
import SitesList from '@/components/sites/SitesList'
import SiteDetail from '@/components/sites/SiteDetail'
import AssignmentDetail from '@/components/assignments/AssignmentDetail'
import AssignmentEmulator from '@/components/assignments/AssignmentEmulator'
import SpecificationsList from '@/components/specifications/SpecificationsList'
import SpecificationDetail from '@/components/specifications/SpecificationDetail'
import DevicesList from '@/components/devices/DevicesList'
import DeviceDetail from '@/components/devices/DeviceDetail'
import DeviceGroupsList from '@/components/groups/DeviceGroupsList'
import DeviceGroupDetail from '@/components/groups/DeviceGroupDetail'
import AssetCategoriesList from '@/components/assets/AssetCategoriesList'
import AssetCategoryDetail from '@/components/assets/AssetCategoryDetail'
import BatchOperationsList from '@/components/batch/BatchOperationsList'
import BatchOperationDetail from '@/components/batch/BatchOperationDetail'
import SchedulesList from '@/components/schedules/SchedulesList'

Vue.use(Router)
Vue.use(Vuetify)
Vue.use(VueMoment)
Vue.use(VueClipboards)
Vue.use(VueHighlightJS)
Vue.use(VueFlatPickr)
Vue.use(Tree)
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
  mode: 'history',
  routes: [
    {
      path: '/',
      component: Login
    }, {
      path: '/system',
      component: SystemAdministration,
      children: [
        {
          path: 'tenants',
          component: TenantsList
        }, {
          path: 'tenants/:tenantId',
          component: TenantDetail
        }, {
          path: 'users',
          component: UsersList
        }
      ]
    }, {
      path: '/tenants/:tenantId',
      component: TenantAdministration,
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
          path: 'assignments/:token',
          component: AssignmentDetail
        }, {
          path: 'assignments/:token/emulator',
          component: AssignmentEmulator
        }, {
          path: 'specifications',
          component: SpecificationsList
        }, {
          path: 'specifications/:token',
          component: SpecificationDetail
        }, {
          path: 'devices',
          component: DevicesList
        }, {
          path: 'devices/:hardwareId',
          component: DeviceDetail
        }, {
          path: 'groups',
          component: DeviceGroupsList
        }, {
          path: 'groups/:token',
          component: DeviceGroupDetail
        }, {
          path: 'assets/categories',
          component: AssetCategoriesList
        }, {
          path: 'assets/categories/:categoryId',
          component: AssetCategoryDetail
        }, {
          path: 'batch',
          component: BatchOperationsList
        }, {
          path: 'batch/:token',
          component: BatchOperationDetail
        }, {
          path: 'schedules',
          component: SchedulesList
        }
      ]
    }
  ]
})
