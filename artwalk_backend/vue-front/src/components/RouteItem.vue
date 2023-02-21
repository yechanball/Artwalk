<template>
  <b-col cols="3" class="my-2">
    <b-card
        img-top
        :img-src="thumbUrl"
        tag="article"
        style="max-width: 20rem;"
        class="mb-2 m-click"
        @click="goDetail"
    >
      <b-card-title>
        {{ route.title }}
      </b-card-title>

      <b-card-text>
        <p>Route Id : {{ route.routeId }}</p>
        <p>{{ nickname }}</p>
        <p>{{ formattedCreation }}</p>
      </b-card-text>

      <b-card-sub-title>
        <p>Made by - {{ nickname }}</p>
      </b-card-sub-title>

    </b-card>
  </b-col>
</template>

<script>

import dayjs from "dayjs";

export default {
  name: "RouteItem.vue",
  props: {
    route: Object,
  },
  data() {
    return {
      date: this.route.creation,
      formattedCreation: dayjs(this.date).format('YYYY/MM/DD'),
      thumbUrl: null,
      nickname: null,
    }
  },
  computed: {
    allUsers() {
      return this.$store.state.user
    }
  },
  created() {
    this.getRouteImage()
    this.getNickName()
  },
  methods: {
    goDetail() {
      this.$router.push({ name: 'routeDetail', params: { routeId: this.route.routeId } })
    },
    getRouteImage() {
      const url = `/route/thumb/${this.route.routeId}`
      const options = {
        headers: {
          'Access-Control-Allow-Origin': '*',
          'accessToken': `Bearer ${this.$store.state.token}`
        }
      }

      fetch(url, options)
          .then(res => res.blob())
          .then(blob => {
            // console.log(blob)
            this.thumbUrl = URL.createObjectURL(blob)
          })
    },
    getNickName() {
      for (var i = 0; i<this.allUsers.length; i++) {
        if (this.allUsers[i].userId === this.route.userId) {
          this.nickname = this.allUsers[i].nickname
        }
      }
    }
  }
}
</script>

<style scoped>

</style>