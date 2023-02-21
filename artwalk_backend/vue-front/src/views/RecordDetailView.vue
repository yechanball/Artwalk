<template>
  <b-container>

    <div class="d-flex justify-content-center align-content-center my-5">
      <h2>{{ nickname }}'s &nbsp; {{ record.title }} </h2>
    </div>

    <!--  레코드 정보  -->
    <b-row class="d-flex">
      <b-col class="col-5">
        <b-img width="400vh" height="300vh" rounded thumbnail fluid :src="thumbUrl"></b-img>
      </b-col>
      <b-col class="col-2 f-size">
        <div class="my-3">Record Id</div>
        <div class="my-3">User Id</div>
        <div class="my-3">Nick Name</div>
        <div class="my-3">Title</div>
        <div class="my-3">Duration</div>
        <div class="my-3">Distance</div>
        <div class="my-3">Creation</div>
      </b-col>
      <b-col class="col-5 f-size">
        <div class="fw-bold my-3">{{ record.recordId }}&nbsp;</div>
        <div class="fw-bold my-3">{{ record.userId }}&nbsp;</div>
        <div class="fw-bold my-3">{{ nickname }}&nbsp;</div>
        <div v-if="record.title" class="fw-bold my-3">{{ record.title }}&nbsp;</div>
        <div v-else class="fw-bold my-3">No Title&nbsp;</div>
        <div class="fw-bold my-3">{{ formattedDuration }}&nbsp;</div>
        <div class="fw-bold my-3">{{ formattedDistance }}&nbsp;</div>
        <div class="fw-bold my-3">{{ formattedCreation }}&nbsp;</div>
      </b-col>

      <!-- 관리자 전용 기록 삭제 -->
      <div class="d-flex justify-content-end my-3">
        <b-button class="btn-danger" v-b-modal.modal-prevent-closing> DELETE </b-button>
        <b-modal
            id="modal-prevent-closing"
            ref="modal"
            title="기록을 삭제하시겠습니까?"
            @show="resetModal"
            @hidden="resetModal"
            @ok="handleOk"
            hide-header-close
        >
          <form ref="form" @submit.stop.prevent="deleteRecord">
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

    </b-row>

  </b-container>
</template>

<script>

import Send from "@/utils/Send";
import dayjs from "dayjs";
import router from "@/router";

export default {
  name: "RecordDetailView.vue",
  data() {
    return {
      record: null,
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
    this.getRecordDetail()
    this.getRecordImage()
  },
  methods: {
    getRecordDetail() {
      return Send({
        method: 'get',
        url: `/record/${this.$route.params.recordId}`,
      })
          .then((res) => {
            this.record = res.data.record
            this.formattedCreation = dayjs(this.record.creation).format('YYYY/MM/DD')
            this.formattedDistance = Math.round(this.record.distance).toLocaleString('ko-KR') + " M"
            this.getTimeStringSeconds(this.record.duration)
            this.getNickName()
          })
          .catch((err) => {
            this.err = err
            // console.log(err)
            router.replace({name: 'error'})
          })
    },
    getRecordImage() {
      const url = `/record/thumb/${this.$route.params.recordId}`
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
      this.deleteRecord()
    },
    deleteRecord() {
      return Send({
        method: 'delete',
        url: `/record/admin/${this.$route.params.recordId}/`,
        headers: {'Content-Type': 'application/json', 'Access-Control-Allow-Origin': '*'},
        data: {
          password: this.pw,
        }
      })
          .then((res) => {
            // console.log(res)
            if (res.data.code === 'Fail') {
              alert("비밀번호를 확인해주세요.")
            } else {
              this.pwState = 1
              this.$nextTick(() => {this.$bvModal.hide('modal-prevent-closing')})
              router.replace({name: 'recordBoard'})
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
        if (this.allUsers[i].userId === this.record.userId) {
          this.nickname = this.allUsers[i].nickname
        }
      }
    }
  }
}
</script>

<style scoped>

</style>