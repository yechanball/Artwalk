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
      <b-card-title v-if="record.title">
        {{ record.title }}
      </b-card-title>

      <b-card-title v-else>
        No Title
      </b-card-title>

      <b-card-text>
        <p>Record Id : {{ record.recordId }}</p>
        <p>{{ nickname }}</p>
        <p>{{ formattedCreation }}</p>
      </b-card-text>

      <b-card-sub-title>
        <p>Duration - {{ formattedDuration }}</p>
        <p>Distance - {{ formattedDistance }}</p>
      </b-card-sub-title>

    </b-card>
  </b-col>
</template>

<script>

import dayjs from "dayjs";

export default {
  name: "RecordItem.vue",
  props: {
    record: Object,
  },
  data() {
    return {
      date: this.record.creation,
      formattedCreation: dayjs(this.date).format('YYYY/MM/DD'),
      formattedDuration: null,
      formattedDistance: Math.round(this.record.distance).toLocaleString('ko-KR') + " M",
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
    this.getTimeStringSeconds(this.record.duration)
    this.getRecordImage()
    this.getNickName()
  },
  methods: {
    goDetail() {
      this.$router.push({ name: 'recordDetail', params: { recordId: this.record.recordId } })
    },
    getTimeStringSeconds(seconds) {
      let hour, min, sec
      hour = parseInt((seconds*1) / 3600)
      min = parseInt((seconds*1 % 3600) / 60)
      sec = Math.floor(seconds*1 % 60)
      if (hour.toString().length == 1) hour = "0" + hour
      if (min.toString().length == 1) min = "0" + min
      if (sec.toString().length == 1) sec = "0" + sec

      this.formattedDuration = hour + ":" + min + ":" + sec
    },
    getRecordImage() {
      const url = `/record/thumb/${this.record.recordId}`
      const options = {
        headers: {
          'Access-Control-Allow-Origin': '*',
          'accessToken': `Bearer ${this.$store.state.token}`
        }
      }

      fetch(url, options)
          .then(res => res.blob())
          .then(blob => {
            this.thumbUrl = URL.createObjectURL(blob)
          })
    },
    getNickName() {
      for (var i = 0; i<this.allUsers.length; i++) {
        if (this.allUsers[i].userId === this.record.userId) {
          this.nickname = this.allUsers[i].nickname
        }
      }
    }
  },
}
</script>

<style scoped>

</style>