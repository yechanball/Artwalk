<template>
  <b-container>

    <div class="d-flex justify-content-center align-content-center my-5">
      <h2>{{ nickname }}'s &nbsp; {{ route.title }} </h2>
    </div>

    <!--  루트 정보  -->
    <b-row class="d-flex">
      <b-col class="col-5">
        <b-img width="400vh" height="300vh" rounded thumbnail fluid :src="thumbUrl"></b-img>
      </b-col>
      <b-col class="col-2 f-size">
        <div class="my-3">Route Id</div>
        <div class="my-3">User Id</div>
        <div class="my-3">Nick Name</div>
        <div class="my-3">Title</div>
        <div class="my-3">Duration</div>
        <div class="my-3">Distance</div>
        <div class="my-3">Creation</div>
      </b-col>
      <b-col class="col-5 f-size">
        <div class="fw-bold my-3">{{ route.routeId }}</div>
        <div class="fw-bold my-3">{{ route.userId }}</div>
        <div class="fw-bold my-3">{{ nickname }}&nbsp;</div>
        <div class="fw-bold my-3">{{ route.title }}</div>
        <div class="fw-bold my-3">{{ formattedDuration }}</div>
        <div class="fw-bold my-3">{{ formattedDistance }}</div>
        <div class="fw-bold my-3">{{ formattedCreation }}</div>
      </b-col>
    </b-row>

    <!-- 관리자 전용 경로 삭제 -->
    <div class="d-flex justify-content-end my-3">
      <b-button class="btn-danger" v-b-modal.modal-prevent-closing> DELETE </b-button>
      <b-modal
          id="modal-prevent-closing"
          ref="modal"
          title="루트를 삭제하시겠습니까?"
          @show="resetModal"
          @hidden="resetModal"
          @ok="handleOk"
          hide-header-close
      >
        <form ref="form" @submit.stop.prevent="deleteRoute">
          <b-form-group
              label="비밀번호를 입력해주세요."
              label-for="name-input"
              invalid-feedback="PW is required"
              :state="pwState"
          >
            <b-form-input
                type="password"
                id="name-input"
                v-model="pw"
                :state="pwState"
                required
            ></b-form-input>
          </b-form-group>
        </form>
      </b-modal>
    </div>

  </b-container>
</template>

<script>
import Send from "@/utils/Send";
import dayjs from "dayjs";
import router from "@/router";

export default {
  name: "RouteDetailView.vue",
  data() {
    return {
      route: null,
      thumbUrl: null,
      formattedCreation: null,
      formattedDuration: null,
      formattedDistance: null,
      pw: null,
      pwState: null,
      nickname: null,
    }
  },
  computed: {
    allUsers() {
      return this.$store.state.user
    }
  },
  created() {
    this.getRouteDetail()
    this.getRouteImage()
  },
  methods: {
    getRouteDetail() {
      return Send({
        method: 'get',
        url: `/route/${this.$route.params.routeId}`,
      })
          .then((res) => {
            this.route = res.data.route
            this.formattedCreation = dayjs(this.route.creation).format('YYYY/MM/DD')
            this.formattedDistance = Math.round(this.route.distance).toLocaleString('ko-KR') + " M"
            this.getTimeStringSeconds(this.route.duration)
            this.getNickName()
          })
          .catch((err) => {
            this.err = err
            // console.log(err)
            router.replace({name: 'error'})
          })
    },
    getRouteImage() {

      const url = `/route/thumb/${this.$route.params.routeId}`
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
    resetModal() {
      this.pw = null
      this.pwState = null
    },
    handleOk(bvModalEvent) {
      bvModalEvent.preventDefault()
      this.deleteRoute()
    },
    deleteRoute() {
      return Send({
        method: 'delete',
        url: `/route/admin/${this.$route.params.routeId}/`,
        headers: {'Content-Type': 'application/json', 'Access-Control-Allow-Origin': '*'},
        data: {
          password: this.pw,
        }
      })
          .then((res) => {
            if (res.data.code === 'Fail') {
              alert("비밀번호를 확인해주세요.")
            } else {
              this.pwState = 1
              this.$nextTick(() => {this.$bvModal.hide('modal-prevent-closing')})
              router.replace({name: 'routeBoard'})
            }
          })
          .catch((err) => {
            alert("잘못된 요청입니다.")
            this.pwState = 0
            this.err = err
            // console.error(err)
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