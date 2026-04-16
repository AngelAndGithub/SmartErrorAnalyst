<template>
  <div class="share-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>错题分享</span>
        </div>
      </template>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <!-- 我分享的 -->
        <el-tab-pane label="我分享的" name="my">
          <div class="share-list">
            <el-table :data="myShares" v-loading="myLoading" border>
              <el-table-column type="index" width="50" />
              <el-table-column label="分享编号" width="180">
                <template #default="{ row }">
                  <el-tag size="small">{{ row.share.shareNo }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="题目内容" min-width="200" show-overflow-tooltip>
                <template #default="{ row }">
                  {{ row.question?.questionContent || '未知题目' }}
                </template>
              </el-table-column>
              <el-table-column label="分享标题" width="150" show-overflow-tooltip>
                <template #default="{ row }">
                  {{ row.share.shareTitle || '-' }}
                </template>
              </el-table-column>
              <el-table-column label="浏览" width="70">
                <template #default="{ row }">
                  {{ row.share.viewCount || 0 }}
                </template>
              </el-table-column>
              <el-table-column label="点赞" width="70">
                <template #default="{ row }">
                  {{ row.share.likeCount || 0 }}
                </template>
              </el-table-column>
              <el-table-column label="解题思路" width="90">
                <template #default="{ row }">
                  <el-badge :value="row.solutionCount || 0" :max="99">
                    <el-button size="small" text>查看</el-button>
                  </el-badge>
                </template>
              </el-table-column>
              <el-table-column label="分享时间" width="160">
                <template #default="{ row }">
                  {{ row.share.createTime }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120" fixed="right">
                <template #default="{ row }">
                  <el-button type="primary" link @click="viewDetail(row.share.shareId)">
                    详情
                  </el-button>
                  <el-button type="danger" link @click="revokeShare(row.share)">
                    撤回
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <el-pagination
              v-model:current-page="myPagination.pageNum"
              v-model:page-size="myPagination.pageSize"
              :total="myPagination.total"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next"
              @size-change="loadMyShares"
              @current-change="loadMyShares"
              class="pagination"
            />
          </div>
        </el-tab-pane>

        <!-- 收到的分享 -->
        <el-tab-pane label="收到的分享" name="received">
          <div class="share-list">
            <el-table :data="receivedShares" v-loading="receivedLoading" border>
              <el-table-column type="index" width="50" />
              <el-table-column label="分享者" width="120">
                <template #default="{ row }">
                  {{ row.sharer?.nickname || row.sharer?.username || '未知用户' }}
                </template>
              </el-table-column>
              <el-table-column label="题目内容" min-width="200" show-overflow-tooltip>
                <template #default="{ row }">
                  {{ row.question?.questionContent || '未知题目' }}
                </template>
              </el-table-column>
              <el-table-column label="分享标题" width="150" show-overflow-tooltip>
                <template #default="{ row }">
                  {{ row.share.shareTitle || '-' }}
                </template>
              </el-table-column>
              <el-table-column label="分享留言" width="200" show-overflow-tooltip>
                <template #default="{ row }">
                  {{ row.share.shareMessage || '-' }}
                </template>
              </el-table-column>
              <el-table-column label="解题思路" width="90">
                <template #default="{ row }">
                  <el-badge :value="row.solutionCount || 0" :max="99">
                    <el-button size="small" text>查看</el-button>
                  </el-badge>
                </template>
              </el-table-column>
              <el-table-column label="分享时间" width="160">
                <template #default="{ row }">
                  {{ row.share.createTime }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="100" fixed="right">
                <template #default="{ row }">
                  <el-button type="primary" link @click="viewDetail(row.share.shareId)">
                    查看
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <el-pagination
              v-model:current-page="receivedPagination.pageNum"
              v-model:page-size="receivedPagination.pageSize"
              :total="receivedPagination.total"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next"
              @size-change="loadReceivedShares"
              @current-change="loadReceivedShares"
              class="pagination"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyShares, getReceivedShares, revokeShare as apiRevokeShare } from '@/api/share'
import type { ShareResultDTO } from '@/api/share'

const router = useRouter()
const activeTab = ref('my')

// 我的分享
const myLoading = ref(false)
const myShares = ref<ShareResultDTO[]>([])
const myPagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 收到的分享
const receivedLoading = ref(false)
const receivedShares = ref<ShareResultDTO[]>([])
const receivedPagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

onMounted(() => {
  loadMyShares()
})

const handleTabChange = (tab: string) => {
  if (tab === 'my') {
    loadMyShares()
  } else {
    loadReceivedShares()
  }
}

const loadMyShares = async () => {
  myLoading.value = true
  try {
    const res = await getMyShares({
      pageNum: myPagination.pageNum,
      pageSize: myPagination.pageSize
    })
    myShares.value = res.records
    myPagination.total = res.total
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    myLoading.value = false
  }
}

const loadReceivedShares = async () => {
  receivedLoading.value = true
  try {
    const res = await getReceivedShares({
      pageNum: receivedPagination.pageNum,
      pageSize: receivedPagination.pageSize
    })
    receivedShares.value = res.records
    receivedPagination.total = res.total
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    receivedLoading.value = false
  }
}

const viewDetail = (shareId: number) => {
  router.push(`/share/detail/${shareId}`)
}

const revokeShare = (share: any) => {
  ElMessageBox.confirm('确定要撤回这个分享吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await apiRevokeShare(share.shareId)
      ElMessage.success('撤回成功')
      loadMyShares()
    } catch (error: any) {
      ElMessage.error(error.message || '撤回失败')
    }
  })
}
</script>

<style scoped>
.share-management {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: 600;
}

.share-list {
  min-height: 400px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
