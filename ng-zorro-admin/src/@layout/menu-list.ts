export const menuList =  [
  {
    id: 1,
    name: '首页',
    ico: 'dashboard',
    link:'/web/home',
    children: []
  },{
    id: 2,
    name: '图表',
    ico: 'bar-chart',
    nzIconfont:'icon--yiliao-jiyin',
    children: [
      {
        id: 21,
        name:'折线图',
        link: '/web/echarts/line',
      },
      {
        id: 22,
        name:'饼图',
        link: '/web/echarts/pie',
      }
    ]
  },{
    id: 3,
    name: '表格',
    ico: 'table',
    children: [
      {
        id: 31,
        name:'Table内编辑',
        link: '/web/table/table-edit',
      },
      {
        id: 32,
        name:'Table综合',
        link: '/web/table/table-complex',
      }
    ]
  },{
    id: 4,
    name: 'Delon模块',
    ico: 'deployment-unit',
    children: [
      {
        id: 41,
        name:'Excel操作',
        link: '/web/xlsx',
      }
    ]
  },{
    id: 5,
    name: '通用组件',
    ico: 'appstore-add',
    children: [
      {
        id: 51,
        name:'富文本',
        link: '/web/components-demo/tinymce',
      },
      {
        id: 53,
        name:'相册',
        link: '/web/components-demo/album',
      },
      {
        id: 54,
        name:'JSON可视化',
        link: '/web/components-demo/json-view',
      }
    ]
  },
  {
    id: 6,
    name: '高德地图',
    ico: 'appstore-add',
    children: [
      {
        id: 61,
        name:'基础地图',
        link: '/web/map/baseMap',
      },

    ]
  },
  {
    id: 7,
    name: '其他杂项',
    ico: 'appstore-add',
    children: [
      {
        id: 71,
        name:'滚动加载',
        link: '/web/other/scrollAddMore',
      },

    ]
  },
  {
    id: 100,
    name: '系统管理',
    ico: 'user-add',
    children: [
      {
        id: 101,
        name:'菜单管理',
        link: '/web/system-manage/menu-manage',
      },
      {
        id: 102,
        name:'角色管理',
        link: '/web/system-manage/role-manage',
      },
      {
        id: 103,
        name:'部门管理',
        link: '/web/system-manage/dept-manage',
      },
      {
        id: 104,
        name:'用户管理',
        link: '/web/system-manage/user-manage',
      }

    ]
  }

]
