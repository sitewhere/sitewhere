<template>
  <div>
    <device-unit-panel :unit="specification.deviceElementSchema">
    </device-unit-panel>
  </div>
</template>

<script>
import DeviceUnitPanel from './DeviceUnitPanel'

export default {

  data: () => ({
  }),

  props: ['specification'],

  components: {
    DeviceUnitPanel
  },

  methods: {
  }
}
</script>

<style scoped>
</style>
