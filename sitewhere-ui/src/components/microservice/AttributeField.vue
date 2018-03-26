<template>
  <v-layout row wrap class="mb-2">
    <v-flex xs4 class="text-xs-right subheading mt-1 pr-4">
      <v-tooltip top v-if="attribute.required">
        <v-icon small slot="activator"
          style="font-size: 8pt; vertical-align: top;" class="red--text">
          fa-asterisk
        </v-icon>
        <span>Attribute is required</span>
      </v-tooltip>
      <strong>{{ attribute.name }}</strong>:
    </v-flex>
    <v-flex xs4>
      <v-text-field v-if="readOnly" :required="attribute.required"
        v-model="attrConstValue" hide-details single-line disabled>
      </v-text-field>
      <v-select v-else-if="attribute.choices"
        :items="attribute.choices" hide-details v-model="attrConstValue"
        item-text="name" item-value="value">
      </v-select>
      <v-text-field v-else-if="attribute.type === 'String'"
        :required="attribute.required" v-model="attrConstValue"
        hide-details single-line>
      </v-text-field>
      <v-text-field v-else-if="attribute.type === 'Integer'"
        :required="attribute.required" type="number"
        v-model="attrConstValue" hide-details single-line>
      </v-text-field>
      <v-checkbox v-else-if="attribute.type === 'Boolean'"
        :label="attribute.name" v-model="attrConstValue"
        hide-details>
      </v-checkbox>
      <site-selector v-else-if="attribute.type === 'SiteReference'"
        selected="attrConstValue">
      </site-selector>
      <device-type-selector
        v-else-if="attribute.type === 'SpecificationReference'"
        v-model="attrConstValue">
      </device-type-selector>
      <scripts-selector v-else-if="attribute.type === 'Script'"
        v-model="attrConstValue" :tenantToken="tenantToken">
      </scripts-selector>
    </v-flex>
    <v-flex xs3 class="pl-2">
      <span v-if="readOnly && attrEnvVar">
        <strong>Env Var</strong>: {{attrEnvVar}}
      </span>
      <v-text-field v-else-if="!readOnly" v-model="attrEnvVar"
        hide-details placeholder="Env Variable">
      </v-text-field>
    </v-flex>
    <v-flex xs1>
      <v-tooltip left class="ml-2">
        <v-icon class="blue--text text--darken-2" slot="activator">
          fa-question-circle
        </v-icon>
        <span>{{ attribute.description }}</span>
      </v-tooltip>
    </v-flex>
  </v-layout>
</template>

<script>
import AreaSelector from '../areas/AreaSelector'
import DeviceTypeSelector from '../devicetypes/DeviceTypeSelector'
import ScriptsSelector from '../tenants/ScriptsSelector'

export default {

  data: () => ({
    attrConstValue: null,
    attrEnvVar: null
  }),

  components: {
    AreaSelector,
    DeviceTypeSelector,
    ScriptsSelector
  },

  props: ['attribute', 'attrValues', 'readOnly', 'tenantToken'],

  created: function () {
    this.onLoadValue(this.attrValues)
  },

  watch: {
    attrValues: function (values) {
      this.onLoadValue(values)
    },
    // Send update if value changed.
    attrConstValue: function (newValue) {
      this.onValueUpdated()
    },
    // Send update if env var changed.
    attrEnvVar: function (newValue) {
      this.onValueUpdated()
    }
  },

  methods: {
    onLoadValue: function (values) {
      if (values) {
        let value = values[this.attribute.localName]
        if (value && value.startsWith('${') && value.endsWith('}')) {
          let content = value.substring(2)
          content = content.substring(0, content.length - 1)
          let parts = content.split(':')
          this.$data.attrConstValue = parts[1]
          this.$data.attrEnvVar = parts[0]
        } else {
          this.$data.attrConstValue = value
          this.$data.attrEnvVar = null
        }
      }
    },
    onValueUpdated: function () {
      let env = this.$data.attrEnvVar
      let val = this.$data.attrConstValue
      let newValue

      if (val && (val.length > 0)) {
        newValue = env ? '${' + env + ':' + val + '}' : val
      } else {
        newValue = env ? '${' + env + '}' : null
      }

      var updated = {}
      updated.attribute = this.attribute
      updated.oldValue = this.$data.attrValue
      updated.newValue = newValue
      this.$emit('valueUpdated', updated)
    }
  }
}
</script>

<style scoped>
.input-group {
  padding: 0;
  margin: 0;
}
</style>
