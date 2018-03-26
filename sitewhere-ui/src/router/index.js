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
import TenantMicroserviceEditor from '@/components/tenants/TenantMicroserviceEditor'
import UsersList from '@/components/users/UsersList'
import GlobalMicroservicesList from '@/components/global/GlobalMicroservicesList'
import GlobalMicroserviceEditor from '@/components/global/GlobalMicroserviceEditor'
import TenantAdministration from '@/components/tenants/TenantAdministration'
import Server from '@/components/server/Server'
import AreasList from '@/components/areas/AreasList'
import AreaDetail from '@/components/areas/AreaDetail'
import AreaTypesList from '@/components/areatypes/AreaTypesList'
import AreaTypeDetail from '@/components/areatypes/AreaTypeDetail'
import AssignmentDetail from '@/components/assignments/AssignmentDetail'
import AssignmentEmulator from '@/components/assignments/AssignmentEmulator'
import DeviceTypesList from '@/components/devicetypes/DeviceTypesList'
import DeviceTypeDetail from '@/components/devicetypes/DeviceTypeDetail'
import DevicesList from '@/components/devices/DevicesList'
import DeviceDetail from '@/components/devices/DeviceDetail'
import DeviceGroupsList from '@/components/devicegroups/DeviceGroupsList'
import DeviceGroupDetail from '@/components/devicegroups/DeviceGroupDetail'
import AssetTypesList from '@/components/assettypes/AssetTypesList'
import AssetTypeDetail from '@/components/assettypes/AssetTypeDetail'
import AssetsList from '@/components/assets/AssetsList'
import AssetDetail from '@/components/assets/AssetDetail'
import BatchOperationsList from '@/components/batch/BatchOperationsList'
import BatchOperationDetail from '@/components/batch/BatchOperationDetail'
import SchedulesList from '@/components/schedules/SchedulesList'

Vue.use(Router)
Vue.use(Vuetify, {
  theme: {
    primary: '#1565c0',
    secondary: '#b0bec5',
    accent: '#8c9eff',
    error: '#b71c1c'
  }
})
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
  // mode: 'history',
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
          path: 'tenants/:tenantToken',
          component: TenantDetail
        }, {
          path: 'tenants/:tenantToken/:identifier',
          component: TenantMicroserviceEditor
        }, {
          path: 'users',
          component: UsersList
        }, {
          path: 'microservices',
          component: GlobalMicroservicesList
        }, {
          path: 'microservices/:identifier',
          component: GlobalMicroserviceEditor
        }
      ]
    }, {
      path: '/tenants/:tenantToken',
      component: TenantAdministration,
      children: [
        {
          path: 'server',
          component: Server
        }, {
          path: 'areas',
          component: AreasList
        }, {
          path: 'areas/:token',
          component: AreaDetail
        }, {
          path: 'areatypes',
          component: AreaTypesList
        }, {
          path: 'areatypes/:token',
          component: AreaTypeDetail
        }, {
          path: 'assignments/:token',
          component: AssignmentDetail
        }, {
          path: 'assignments/:token/emulator',
          component: AssignmentEmulator
        }, {
          path: 'devicetypes',
          component: DeviceTypesList
        }, {
          path: 'devicetypes/:token',
          component: DeviceTypeDetail
        }, {
          path: 'devices',
          component: DevicesList
        }, {
          path: 'devices/:token',
          component: DeviceDetail
        }, {
          path: 'groups',
          component: DeviceGroupsList
        }, {
          path: 'groups/:token',
          component: DeviceGroupDetail
        }, {
          path: 'assettypes',
          component: AssetTypesList
        }, {
          path: 'assettypes/:token',
          component: AssetTypeDetail
        }, {
          path: 'assets',
          component: AssetsList
        }, {
          path: 'assets/:token',
          component: AssetDetail
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
