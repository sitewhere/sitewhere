<template>
  <div v-if="tenant">
    <v-app>
      <tenant-detail-header :tenant="tenant" class="mb-3">
      </tenant-detail-header>
      <v-tabs class="elevation-2" dark v-model="active">
        <v-tabs-bar slot="activators" class="blue darken-2">
          <v-tabs-slider class="blue lighten-3"></v-tabs-slider>
          <v-tabs-item key="configuration" href="#configuration">
            Tenant Configuration
          </v-tabs-item>
        </v-tabs-bar>
        <v-tabs-content key="configuration" id="configuration">
          <div v-html="wizardContent"></div>
        </v-tabs-content>
      </v-tabs>
    </v-app>
  </div>
</template>

<script>
import TenantDetailHeader from './TenantDetailHeader'
import {
  wizard,
  resetWizard,
  onAddChild, // eslint-disable-line no-unused-vars
  onChildOpenClicked // eslint-disable-line no-unused-vars
} from './TenantConfigEditor'
import {
  _getTenant,
  _getTenantConfiguration,
  _getTenantConfigurationModel,
  _getTenantConfigurationRoles
} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    tenantId: null,
    tenant: null,
    tenantConfig: null,
    tenantConfigModel: null,
    tenantConfigRoles: null,
    wizardContent: null,
    active: null
  }),

  components: {
    TenantDetailHeader
  },

  created: function () {
    this.$data.tenantId = this.$route.params.tenantId
    this.refresh()
  },

  computed: {
    configDataAvailable: function () {
      return this.$data.tenantConfig && this.$data.tenantConfigModel &&
        this.$data.tenantConfigRoles
    }
  },

  watch: {
    configDataAvailable: function (available) {
      console.log('Configuration loaded.')
      wizard.config = this.$data.tenantConfig
      wizard.configModel = this.$data.tenantConfigModel
      wizard.roles = this.$data.tenantConfigRoles
      this.$data.wizardContent = resetWizard()
    }
  },

  methods: {
    // Called to refresh data.
    refresh: function () {
      // Load information.
      var component = this
      _getTenant(this.$store, this.$data.tenantId)
        .then(function (response) {
          component.onLoaded(response.data)
        }).catch(function (e) {
        })
      _getTenantConfiguration(this.$store, this.$data.tenantId)
        .then(function (response) {
          component.$data.tenantConfig = response.data
        }).catch(function (e) {
        })
      _getTenantConfigurationModel(this.$store)
        .then(function (response) {
          component.$data.tenantConfigModel = response.data
        }).catch(function (e) {
        })
      _getTenantConfigurationRoles(this.$store)
        .then(function (response) {
          component.$data.tenantConfigRoles = response.data
        }).catch(function (e) {
        })
    },

    // Called after data is loaded.
    onLoaded: function (tenant) {
      this.$data.tenant = tenant
      var section = {
        id: 'tenants',
        title: 'Manage Tenant',
        icon: 'layers',
        route: '/tenants/' + tenant.id,
        longTitle: 'Manage Tenant: ' + tenant.id
      }
      this.$store.commit('currentSection', section)
    }
  }
}
</script>

<style>
div.wz-header {
	border: 1px solid #666;
	background-color: #eee;
	padding: 13px;
	margin-bottom: 10px;
	-webkit-box-shadow: 4px 4px 4px 0px rgba(192, 192, 192, 0.5);
	-moz-box-shadow: 4px 4px 4px 0px rgba(192, 192, 192, 0.5);
	box-shadow: 4px 4px 4px 0px rgba(192, 192, 192, 0.5);
}

div.wz-header h1 {
	font-size: 26px;
	line-height: 1em;
	vertical-align: top;
	margin: 1px;
	display: inline;
}

div.wz-header h2 {
	font-size: 16px;
	margin: 0;
	margin-top: 15px;
	line-height: 1.1em;
	font-weight: normal;
	clear: both;
	line-height: 1.1em;
}

.wz-header-icon {
	float: left;
	padding-right: 10px;
	font-size: 26px;
}

.wz-drag-icon {
	padding: 10px 10px 10px 5px;
	font-size: 20px;
	color: #ccc;
	cursor: move;
	float: left;
	border-right: 1px solid #ccc;
}

.wz-role {
	border: 1px solid #999;
	padding: 15px 10px 10px;
	position: relative;
	margin: 10px 0px 25px;
	box-shadow: 4px 4px 4px 0px rgba(192, 192, 192, 0.3);
}

.wz-role-label {
	position: absolute;
	top: -10px;
	left: 5px;
	font-size: 12px;
	background-color: #999;
	color: #fff;
	padding: 1px 5px;
}

.wz-role-required {
	border-width: 2px;
}

.wz-role-label-required {
	background-color: #666;
}

.wz-role-missing {
	border: 1px solid #cc3;
	background-color: #ffe;
}

.wz-role-missing-optional {
	border: 1px solid #eee;
	box-shadow: none;
}

.wz-role-label-missing {

}

.wz-child {
	border: 1px solid #ccc;
	background-color: #eee;
	padding: 5px;
	margin-bottom: 5px;
	list-style-type: none;
	list-style-position: inside;
}

.wz-child-required {
	border-width: 2px;
}

.wz-child-missing {
	border-style: dashed;
	border-color: #999;
}

.wz-child .wz-child-icon {
	float: left;
	padding: 8px 10px;
	font-size: 22px;
}

.wz-child .wz-child-name {
	display: inline;
	font-size: 20px;
	padding: 0;
	margin: 0;
}

.wz-child .wz-child-nav {
	float: right;
	padding: 9px;
}

.wz-sortable-placeholder {
	min-height: 40px;
	border: 2px dashed #aaa;
	background-color: #ccc;
	padding: 5px;
	margin-bottom: 5px;
	list-style-type: none;
	list-style-position: inside;
	border: 2px dashed #aaa;
}

.wz-sortable-item {

}

.dd-icon {
	width: 20px;
}

div.wz-divider {
	clear: both;
	padding-top: 10px;
	margin-top: 10px;
	border-top: 1px solid #ddd;
}

ol.wz-breadcrumb {
	margin-top: 8px;
	margin-bottom: -2px;
	margin-right: 6px;
	margin-left: 6px;
	padding: 2px 8px;
	border: 1px solid #eee;
	border-radius: 0px;
	font-size: 12px;
	background-color: #f9f9f9;
}

.sw-attribute-group {
	border: 1px solid #ccc;
	padding: 25px 10px 10px;
	margin-bottom: 20px;
	position: relative;
	margin-top: 5px;
}

.sw-attribute-group h1 {
	margin: 0;
	padding: 2px 5px;
	font-size: 12px;
	line-height: 1em;
	position: absolute;
	background-color: #666;
	color: #fff;
	top: -9px;
}

label.sw-control-label {
	font-weight: bold;
	font-size: 17px;
	width: 250px;
}

label.sw-control-label i {
	color: #ccc;
	padding-left: 5px;
	margin-top: 2px;
	vertical-align: top;
	font-size: 10px;
}

div.sw-controls {
	margin-left: 290px;
	font-size: 17px;
	line-height: 1.7em;
}

div.wz-button-bar {
	padding: 10px 0px;
	margin-top: 10px;
	border-top: 1px solid #ddd;
}
</style>
