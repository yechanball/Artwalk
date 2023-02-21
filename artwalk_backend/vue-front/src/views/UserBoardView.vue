<template>
  <b-container>
    <div class="d-flex justify-content-center align-content-center mt-5 mb-1">
      <h1>User Board</h1>
    </div>
    <br>
    <div>

      <!--   드롭 다운 버튼   -->
      <div class="d-flex justify-content-end">
        <b-dropdown id="dropdown-1" :text="selectedDropdownItem" class="m-1" style="height: 38px;">
          <b-dropdown-item v-for="option in options" :key="option" :value="option" @click="changeCategory(option)">
            {{ option }}
          </b-dropdown-item>
        </b-dropdown>
        <b-form @submit.prevent="goSearch" @reset.prevent="doReset" class="d-block">
          <b-form-input :placeholder="selectedDropdownItem" v-model="searchKeyword" class="w-100 d-inline-block m-1">
          </b-form-input>
          <div class="d-flex justify-content-end">
            <b-button type="submit" style="background-color: #3c3c3c; border-color: #3c3c3c;" class="m-1">Submit</b-button>
            <b-button type="reset" style="background-color: rgba(211,47,47,0.8); border-color: rgba(211,47,47,0.8);" class="m-1">Reset</b-button>
          </div>
        </b-form>
      </div>

      <br>

      <!--  게시판 (기본) -->
      <b-table v-if="searchedUsers == null" :fields="fields" :items="allUsers" responsive no-sort-reset label-sort-asc="" label-sort-desc="" :per-page="perPage" :current-page="currentPage">
        <template #cell(nickname)="data">
          <router-link :to="{ name: 'userDetail', params: { userId: data.item.userId } }" class="tdn maincolor">
            {{ data.item.nickname }}
          </router-link>
        </template>
        <!--        <template #cell(profile)="data">-->
        <!--          <b-img width="50vh" height="50vh" rounded thumbnail fluid :src="data.item.profile"></b-img>-->
        <!--        </template>-->
      </b-table>

      <!--   검색한 결과 경로가 1개 이상일 때   -->
      <b-table v-else-if="searchedUsers.length >= 1" :fields="fields" :items="searchedUsers" responsive no-sort-reset label-sort-asc="" label-sort-desc="" :per-page="perPage" :current-page="currentPage">
        <template #cell(nickname)="data">
          <router-link :to="{ name: 'userDetail', params: { userId: data.item.userId } }" class="tdn maincolor">
            {{ data.item.nickname }}
          </router-link>
        </template>
        <!--        <template #cell(profile)="data">-->
        <!--          <b-img width="50vh" height="50vh" rounded thumbnail fluid :src="data.item.profile"></b-img>-->
        <!--        </template>-->
      </b-table>

      <!--   검색한 결과 경로가 없을 때   -->
      <div v-else-if="searchedUsers.length < 1">
        <p> 검색 결과 없음 </p>
      </div>

      <!--   페이지네이션   -->
      <b-pagination
          size="sm"
          align="center"
          :total-rows="rows"
          v-model="currentPage"
          :per-page="perPage"
          class="custom-pagination"
      />

    </div>
    <UserItem/>
  </b-container>
</template>

<script>
import UserItem from "@/components/UserItem.vue";
import Send from "@/utils/Send";

export default {
  name: "UserBoardView.vue",
  components: {
    UserItem
  },
  computed: {
    allUsers() {
      return this.$store.state.user
    },
    rows() {
      return this.allUsers.length
    }
  },
  data() {
    return {
      currentPage: 1,
      perPage: 10,
      selectedDropdownItem: "not Selected",
      searchKeyword: null,
      searchedUsers: null,
      options: ['userId', 'nickname'],
      fields: [
        {
          key: 'userId',
          label: 'User Id',
          tdClass: 'align-middle'
        },
        {
          key: 'nickname',
          label: 'Nickname',
          tdClass: 'align-middle',
        },
        // {
        //   key: 'profile',
        //   label: 'Profile Image',
        // },
        {
          key: 'userType',
          label: 'User Type',
          formatter: value => {
            return (value ? 'Social':'App')
          },
          tdClass: 'align-middle',
          sortable: true
        },
        // {
        //   key: 'level',
        //   label: 'Level',
        //   tdClass: 'align-middle',
        //   sortable: true
        // },
        // {
        //   key: 'exp',
        //   label: 'Exp',
        //   tdClass: 'align-middle',
        //   sortable: true
        // },
        {
          key: 'userRouteCount',
          label: "Routes",
          tdClass: 'align-middle',
          sortable: true
        },
        {
          key: 'userRecordCount',
          label: "Records",
          tdClass: 'align-middle',
          sortable: true
        },
        {
          key: 'regDate',
          label: "Registration Date",
          tdClass: 'align-middle',
          sortable: true
        },
        {
          key: 'recentAccess',
          label: "Recent Connections",
          tdClass: 'align-middle',
          sortable: true
        },
      ]
    }
  },
  created() {
    this.$store.dispatch("getUser");
  },
  methods: {
    changeCategory(e) {
      this.selectedDropdownItem = e
    },
    doReset() {
      this.selectedDropdownItem = "not Selected"
      this.searchKeyword = null
      this.searchedUsers = null
    },
    goSearch() {
      if (this.selectedDropdownItem === 'not Selected') {
        alert("검색 타입을 지정해주세요.")
        return
      } else if (this.searchKeyword === null) {
        alert("검색어를 입력해주세요.")
        return
      }
      return Send({
        method: 'get',
        url: '/user/search',
        params: {
          type : this.selectedDropdownItem,
          keyword : this.searchKeyword
        }
      })
          .then((res) => {
            this.searchedUsers = res.data.users
          })
          .catch((err) => {
            console.log(err)
          })
    },
  }
}
</script>

<style scoped>

</style>