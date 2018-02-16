<template>
  <v-card hover color="white">
    <v-container fluid grid-list-lg>
      <v-layout row>
        <v-flex xs2>
          <v-card-media>
            <v-icon class="atlogo">{{ areaType.icon }}</v-icon>
          </v-card-media>
        </v-flex>
        <v-flex xs10>
          <div>
            <div class="headline">{{ areaType.name }}</div>
            <div class="atdesc">{{ areaType.description }}</div>
            <v-card-actions>
              <v-spacer></v-spacer>
              <area-type-update-dialog
                :token="areaType.token" :areaTypes="areaTypes"
                @areaTypeUpdated="onAreaTypeUpdated">
              </area-type-update-dialog>
              <area-type-delete-dialog :token="areaType.token"
                @areaTypeDeleted="onAreaTypeDeleted">
              </area-type-delete-dialog>
            </v-card-actions>
          </div>
        </v-flex>
      </v-layout>
    </v-container>
  </v-card>
</template>

<script>
import AreaTypeUpdateDialog from './AreaTypeUpdateDialog'
import AreaTypeDeleteDialog from './AreaTypeDeleteDialog'

export default {

  data: () => ({
  }),

  components: {
    AreaTypeUpdateDialog,
    AreaTypeDeleteDialog
  },

  props: ['areaType', 'areaTypes'],

  methods: {
    // Called when an area type is updated.
    onAreaTypeUpdated: function () {
      this.$emit('areaTypeUpdated', this.areaType.token)
    },
    // Called when an area type is deleted.
    onAreaTypeDeleted: function () {
      this.$emit('areaTypeDeleted', this.areaType.token)
    }
  }
}
</script>

<style scoped>
.atlogo {
  font-size: 45pt;
}
.atdesc {
  height: 40px;
  overflow-y: hidden;
}
</style>
