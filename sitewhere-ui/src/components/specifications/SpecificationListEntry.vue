<template>
  <v-card hover class="spec white pa-2">
    <v-card-text @click="onOpenSpecification">
      <div class="spec-logo"
        v-bind:style="{ 'background': 'url(' + specification.assetImageUrl + ')', 'background-size': 'contain', 'background-repeat': 'no-repeat', 'background-position': '50% 50%'}">
      </div>
      <div class="spec-name">{{specification.name}}</div>
      <div class="spec-desc">{{specification.asset.description}}</div>
    </v-card-text>
  </v-card>
</template>

<script>
export default {

  data: () => ({
  }),

  props: ['specification'],

  methods: {
    // Called when a specification is clicked.
    onOpenSpecification: function () {
      var tenant = this.$store.getters.selectedTenant
      if (tenant) {
        this.$router.push('/tenants/' + tenant.id + '/specifications/' +
          this.specification.token)
      }
    }
  }
}
</script>

<style scoped>
.spec {
  min-height: 180px;
  overflow-y: hidden;
}

.spec-logo {
  position: absolute;
  top: 0px;
  left: 0px;
  bottom: 0px;
  width: 140px;
}

.spec-name {
  position: absolute;
  top: 5px;
  left: 158px;
  right: 10px;
  font-size: 22px;
  font-weight: 400;
  white-space: nowrap;
  overflow-x: hidden;
}

.spec-desc {
  position: absolute;
  top: 42px;
  left: 160px;
  right: 10px;
  bottom: 10px;
  font-size: 14px;
  overflow-y: hidden;
}
</style>
