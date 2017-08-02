<template>
  <v-card v-if="operation" class="white">
    <v-card-text class="operation">
      <v-icon class="operation-icon grey--text" fa>cogs</v-icon>
      <div class="operation-headers">
        <header-field label="Token">
          <clipboard-copy-field :field="operation.token"
            message="Operation token copied to clipboard">
          </clipboard-copy-field>
        </header-field>
        <header-field label="Operation type">
          <span>{{ operation.operationType }}</span>
        </header-field>
        <header-field label="Status">
          <span>{{ operation.processingStatus }}</span>
        </header-field>
        <header-field label="Created">
          <span>{{ formatDate(operation.createdDate) }}</span>
        </header-field>
        <header-field label="Processing Started">
          <span>{{ formatDate(operation.processingStartedDate) }}</span>
        </header-field>
        <header-field label="Processing Finished">
          <span>{{ formatDate(operation.processingEndedDate) }}</span>
        </header-field>
      </div>
    </v-card-text>
  </v-card>
</template>

<script>
import Utils from '../common/Utils'
import HeaderField from '../common/HeaderField'
import ClipboardCopyField from '../common/ClipboardCopyField'

export default {

  data: function () {
    return {
    }
  },

  components: {
    HeaderField,
    ClipboardCopyField
  },

  props: ['operation'],

  computed: {
    rolesView: function () {
      return this.group.roles.join(', ')
    }
  },

  methods: {
    // Fire event to have parent refresh content.
    refresh: function () {
      this.$emit('refresh')
    },

    // Format date.
    formatDate: function (date) {
      return Utils.formatDate(date)
    }
  }
}
</script>

<style scoped>
.operation {
  position: relative;
  min-height: 190px;
  overflow-x: hidden;
}
.operation-icon {
  position: absolute;
  top: 0px;
  left: 0px;
  bottom: 0px;
  width: 200px;
  font-size: 90px;
  background-color: #eee;
  border-right: 1px solid #ddd;
  text-align: center;
  vertical-align: middle;
  padding: 40px 50px;
}

.operation-headers {
  position: absolute;
  top: 20px;
  left: 220px;
}
</style>
