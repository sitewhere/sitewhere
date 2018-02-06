<template>
  <v-card hover class="white">
    <v-card-text class="category" @click="onOpenCategory">
      <v-icon style="font-size: 28pt;" class="category-icon grey--text">
        fa-{{ getIconForCategory(category) }}
      </v-icon>
      <div class="category-name title">
        {{ utils.ellipsis(category.name, charWidth) }}
      </div>
      <div class="category-id subheading">
        {{ utils.ellipsis(category.id, charWidth) }}
      </div>
    </v-card-text>
  </v-card>
</template>

<script>
import Utils from '../common/Utils'

export default {

  data: function () {
    return {
      charWidth: 40,
      utils: Utils
    }
  },

  components: {
  },

  props: ['category'],

  methods: {
    // Called when a site is clicked.
    onOpenCategory: function () {
      this.$emit('categoryOpened', this.category)
    },
    // Get icon displayed for a given category.
    getIconForCategory: function (category) {
      if (category.assetType === 'Device') {
        return 'mobile'
      } else if (category.assetType === 'Hardware') {
        return 'laptop'
      } else if (category.assetType === 'Person') {
        return 'user-circle'
      } else if (category.assetType === 'Location') {
        return 'map-marker'
      }
      return 'question-circle'
    },
    // Fire event to have parent refresh content.
    refresh: function () {
      this.$emit('refresh')
    }
  }
}
</script>

<style scoped>
.category {
  position: relative;
  min-height: 70px;
  overflow-x: hidden;
}
.category-icon {
  position: absolute;
  top: 0px;
  left: 0px;
  bottom: 0px;
  width: 90px;
  background-color: #eee;
  border-right: 1px solid #ddd;
  text-align: center;
}
.category-name {
  position: absolute;
  top: 10px;
  left: 100px;
  color: #333;
  font-weight: 700;
}
.category-id {
  position: absolute;
  top: 37px;
  left: 100px;
  background-color: #fff;
}
</style>
