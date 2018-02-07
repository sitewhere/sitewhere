<template>
  <div class="command-content">
    <div class='command-desc'>/** {{ command.description }} **/</div>
    <span class="command-name" @click.native="commandClicked">
      {{ command.name }}
    </span>
    (
    <span v-for="(parameter, index) in command.parameters" :key="parameter.name">
      <span v-if="index > 0"> ,</span>
      <strong v-if="parameter.required">
        <span class="command-param-name">
          {{ parameter.name }}
        </span>:
        <span class="command-param-type">
          {{ parameter.type }}
        </span>
      </strong>
      <span v-else>
        <span class="command-param-name">
          {{ parameter.name }}
        </span>:
        <span class="command-param-type">
          {{ parameter.type }}
        </span>
      </span>
    </span>
    )
  </div>
</template>

<script>
export default {

  data: () => ({
  }),

  props: ['command'],

  methods: {
  }
}
</script>

<style scoped>
.command-content {
  font-family: 'courier';
  font-size: 14px;
  width: 100%;
}

.command-token {
  font-weight: 700;
  background-color: #eee;
  padding: 10px;
  border: 1px solid #ccc;
}

.command-desc {
	color: #060;
	font-style: italic;
	max-width: 90%;
}

.command-name {
	color: #060;
	font-weight: bold;
}

.command-param-name {
	color: #333;
}

.command-param-type {
	color: #666;
}
</style>
