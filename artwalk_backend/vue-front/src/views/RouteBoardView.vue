<template>
  <b-container>
    <div class="d-flex justify-content-center align-content-center mt-5 mb-1">
      <h1>Route Board</h1>
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

      <!--   루트 리스트 카드 - RouteItem 으로 props   -->
      <b-row v-if="searchedRoutes == null">
        <RouteItem
            v-for="route in paginatedData"
            :key="route.routeId"
            :route="route"
        />
      </b-row>

      <!--   검색한 결과 경로가 1개 이상일 때   -->
      <b-row v-else-if="searchedRoutes.length >= 1">
        <RouteItem
            v-for="route in paginatedData"
            :key="route.routeId"
            :route="route"
        />
      </b-row>

      <!--   검색한 결과 경로가 없을 때   -->
      <div v-else-if="searchedRoutes.length < 1">
        <p> 검색 결과 없음 </p>
      </div>

      <b-pagination
          size="sm"
          align="center"
          v-if="totalRows > 0"
          :total-rows="totalRows"
          :per-page="perPage"
          v-model="pageNum"
          class="custom-pagination"
      />

    </div>
  </b-container>
</template>

<script>

import RouteItem from "@/components/RouteItem.vue";
import Send from "@/utils/Send";

export default {
  name: "RouteBoardView.vue",
  components: {
    RouteItem,
  },
  data() {
    return {
      selectedDropdownItem: "not Selected",
      searchKeyword: null,
      searchedRoutes: null,
      options: ['nickname', 'title'],
      perPage: 12,
      pageNum: 1
    }
  },
  methods: {
    changeCategory(e) {
      this.selectedDropdownItem = e
    },
    goSearch() {
      if (this.selectedDropdownItem === "not Selected") {
        alert("검색 타입을 지정해주세요.")
        return
      } else if (this.searchKeyword === null) {
        alert("검색어를 입력해주세요.")
        return
      }

      return Send({
        method: 'get',
        url: '/route/search/',
        params: {
          type: this.selectedDropdownItem,
          keyword: this.searchKeyword
        }
      })
          .then((res) => {
            this.pageNum = 1
            this.searchedRoutes = res.data.routes
          })
          .catch((err) => {
            this.err = err
            // console.log(err)
          })
    },
    doReset() {
      this.selectedDropdownItem = "not Selected"
      this.searchKeyword = null
      this.searchedRoutes = null
      this.pageNum = 1
    },
  },
  computed: {
    allRoutes() {
      return this.$store.state.route
    },
    totalRows() {
      if (this.searchedRoutes == null) {
        return this.allRoutes.length;
      } else if (this.searchedRoutes.length >= 1) {
        return this.searchedRoutes.length;
      } else {
        return 0
      }
    },
    paginatedData() {
      const start = (this.pageNum-1) * this.perPage,
          end = start + this.perPage;
      if (this.searchedRoutes == null) {
        return this.allRoutes.slice(start, end);
      } else if (this.searchedRoutes.length >= 1) {
        return this.searchedRoutes.slice(start, end);
      } else {
        return []
      }
    },
  },
  created() {
    this.$store.dispatch('getRoute')
  },
}
</script>

<style scoped>

</style>