<template>
  <span class="actions-block">
    <v-btn dark icon small
      class="blue--text text--darken-4 ml-0 mr-0"
      v-tooltip:top="{ html: 'Edit' }"
      @click.native.stop="onOpenEdit">
      <v-icon fa class="fa-lg">edit</v-icon>
    </v-btn>
    <v-btn dark icon small
      class="red--text text--darken-4 ml-0"
      v-tooltip:top="{ html: 'Delete' }"
      @click.native.stop="onOpenDelete">
      <v-icon fa class="fa-lg">times</v-icon>
    </v-btn>
    <slot name="edit"></slot>
    <slot name="delete"></slot>
  </span>
</template>

<script>
export default {

  data: () => ({
  }),

  mounted: function () {
    this.$slots['edit'][0].componentInstance.$on('edited', this.onEdited)
    this.$slots['delete'][0].componentInstance.$on('deleted', this.onDeleted)
  },

  methods: {
    onOpenEdit: function () {
      this.$slots['edit'][0].componentInstance.onOpenDialog()
    },
    onOpenDelete: function () {
      this.$slots['delete'][0].componentInstance.showDeleteDialog()
    },
    onEdited: function () {
      this.$emit('edited')
    },
    onDeleted: function () {
      console.log('deleted')
      this.$emit('deleted')
    }
  }
}
</script>

<style scoped>
.actions-block {
  display: inline-block;
  white-space: nowrap;
}
</style>
