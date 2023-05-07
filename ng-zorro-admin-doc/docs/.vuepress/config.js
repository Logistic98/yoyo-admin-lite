module.exports = {
  title: 'ng-zorro-admin',
  description: 'A magical angular admin',
   head: [
    ['link', { rel: 'icon', href: '/favicon.ico' }]
  ],
  base: "/",
  // configureWebpack: {
  //   resolve: {
  //     alias: {
  //       '@alias': 'path/to/some/dir'
  //     }
  //   }
  // },
  //导航栏链接
  themeConfig: {
    nav: [
      { text: '指南', link: '/guide/' },
      {
        text: '功能',
        items:[
          {
            text:'组件',
            items:[
              {text:'富文本',link:'/feater/rich-editor/'},
              {text:'返回顶部',link:'/feater/go2top/'},
            ]
          },
          {
            text:'工具',
            items:[
              {text:'lodashjs',link:'/feater/lodashjs/'},
              {text:'utils',link:'/feater/utils/'},
            ]
          }
        ]
      },
     
    ],
     sidebar: {
      '/guide/':[
        {
          title: '基础',
          collapsable: false,
          children: [
            ['', '介绍'],
            ['layout', '布局'],
            ['newPage', '新增页面'],
            ['style', '样式'],
            ['login', '登录'],
            ['authority', '权限验证'],
            
          ],
        },
        {
          title: '进阶',
          collapsable: false,
          children: [
            ['injectable', '依赖注入'],
            ['content', '内容映射'],
            ['reference', '引用类型'],
          ],
        }
      ],
      '/feater/': [
        { title: '富文本', path: 'rich-editor'}, /* /feater/rich-editor.html */
        { title: '返回顶部', path: 'go2top'},
        { title: 'lodashjs', path: 'lodashjs'},
        { title: 'utils', path: 'utils'},
      ],
     }
  }
}