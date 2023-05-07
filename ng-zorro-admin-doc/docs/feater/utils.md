### 数值转人民币
```js
/**
 * 转化成RMB元字符串
 * @param number
 * @param digits 当数字类型时，允许指定小数点后数字的个数，默认2位小数
 */
import{yuan} from '@utils'
 money = yuan(200,2)
// ¥200.00
```
```html
<div [innerHTML]="money"></div>
<!-- ¥ 200.00 -->
```

### 全角转半角
```js
/**
 *  @param {string}
 */
import{ToCDB} from '@utils'
ToCDB('ｑｕａｎｊｉａｏ')
//quanjiao
```

### 格式化金额
```js
import{moneyFormat} from '@utils'
/**
 * @param num  金额
 * @param [format]  需保留的小数，不设置时原样输出
 */
moneyFormat(3000,2)   //  "3,000.00"
```


### 价格转换浮点型 
把moneyFormat格式化后的金额转成number类型
```js
import{rmoney} from '@utils'
rmoney('20,100') // 20100
```

### 加密电话号码
```js
/**
 * @param {string}  电话号码
 */
import{phoneFormat} from '@utils'
phoneFormat('13181620271')
//131****0271

```