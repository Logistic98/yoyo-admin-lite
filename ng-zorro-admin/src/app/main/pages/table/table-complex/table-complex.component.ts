import { Component, OnInit } from '@angular/core';
import { Data } from '@angular/router';
import { format } from 'date-fns'
import { timer } from 'rxjs';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { NzModalService } from 'ng-zorro-antd/modal';
interface ItemData {
  id: number;
  name: string;
  gender:number;
  score:number;
  phone: string;
  idcard:string;
  socialcode:string;
  address: string;
  time:string;
}
@Component({
  selector: 'app-table-complex',
  templateUrl: './table-complex.component.html',
  styleUrls: ['./table-complex.component.less']
})
export class TableComplexComponent implements OnInit {

  date = null;

  loading = true
  total = 10
  checked = false;
  indeterminate = false;
  listOfCurrentPageData: readonly ItemData[] = [];
  listOfData: readonly ItemData[] = [];
  setOfCheckedId = new Set<number>();

  isVisible  = false
  isOkLoading = false
  validateForm!: FormGroup;

  page = 1
  pageSize = 20
  searchContent = ''
  gender = ''
  startDate = ''
  endDate = ''
  sexFilters = [
    { text: '男', value: 1 },
    { text: '女', value: 2 }
  ]

  constructor(private fb: FormBuilder,private modal: NzModalService) { }

  ngOnInit(): void {
    this.listOfData = new Array(10).fill(0).map((_, index) => ({
      id: index,
      gender: Math.random()>0.5?1:2,
      score: index,
      name: `Edward King ${index}`,
      phone: '13182727262',
      idcard: '110101199003071233',
      socialcode:'110292',
      address:'山东省',
      time:'2021-12-5 13:42'
    }));
    timer(2000).subscribe(()=>{
      this.loading = false
    })

    //编辑
    this.validateForm = this.fb.group({
      name: ['',Validators.required],
      phone: ['',[Validators.required,Validators.pattern(/^(?:(?:\+|00)86)?1[3-9]\d{9}$/)]],
      idcard: ['',[Validators.required,Validators.pattern(/^\d{6}((((((19|20)\d{2})(0[13-9]|1[012])(0[1-9]|[12]\d|30))|(((19|20)\d{2})(0[13578]|1[02])31)|((19|20)\d{2})02(0[1-9]|1\d|2[0-8])|((((19|20)([13579][26]|[2468][048]|0[48]))|(2000))0229))\d{3})|((((\d{2})(0[13-9]|1[012])(0[1-9]|[12]\d|30))|((\d{2})(0[13578]|1[02])31)|((\d{2})02(0[1-9]|1\d|2[0-8]))|(([13579][26]|[2468][048]|0[048])0229))\d{2}))(\d|X|x)$/)]],
      socialcode: ['',Validators.required],
      address: ['',Validators.required],
      time: ['',Validators.required]
    });
  }

  search(){
    console.log('ss')
  }
  dateChange(result: Date[]): void {
    console.log('onChange: ', format(result[0],'yyyy/MM/dd'),format(result[1],'yyyy/MM/dd'));
    this.startDate = format(result[0],'yyyy/MM/dd')
    this.endDate = format(result[1],'yyyy/MM/dd')
  }

  updateCheckedSet(id: number, checked: boolean): void {
    if (checked) {
      this.setOfCheckedId.add(id);
    } else {
      this.setOfCheckedId.delete(id);
    }
  }

  onItemChecked(id: number, checked: boolean): void {
    this.updateCheckedSet(id, checked);
    this.refreshCheckedStatus();
    console.log([...this.setOfCheckedId])
  }

  onAllChecked(value: boolean): void {
    this.listOfCurrentPageData.forEach(item => this.updateCheckedSet(item.id, value));
    this.refreshCheckedStatus();
    console.log([...this.setOfCheckedId])
  }

  onCurrentPageDataChange($event: readonly ItemData[]): void {
    this.listOfCurrentPageData = $event;
    this.refreshCheckedStatus();
    //console.log($event)
  }

  refreshCheckedStatus(): void {
    this.checked = this.listOfCurrentPageData.every(item => this.setOfCheckedId.has(item.id));
    this.indeterminate = this.listOfCurrentPageData.some(item => this.setOfCheckedId.has(item.id)) && !this.checked;
  }

  removeAllChecked(){
    console.log([...this.setOfCheckedId])
  }

  exportAllChecked(){
    console.log([...this.setOfCheckedId])
  }

  sortScoreFn(a: Data, b: Data){
    return  a.score - b.score
  }

  sexFiltersFn(list: string[], item: Data){
    return list.some(gender => item.gender === gender)
  }
  sexFilterHandle(e:any){
    console.log(e)
    this.gender = e
    /*需后端筛查的时候用*/
  }


  edit(data:any){
    console.log(data)
    this.isVisible = true;
    this.validateForm.patchValue(data)
  }
  remove(data:any){
    this.modal.confirm({
      nzTitle: '<i>确定删除这条信息吗?</i>',
      nzContent: '',
      nzOnOk: () => {
        this.listOfData = this.listOfData.filter((d:any)=>{
          return d.id!==data.id
        })
        //console.log(this.listOfData)
      }
    });
  }
  export(data:any){

  }

  modalCancel(){
    this.isVisible = false;
  }

  modalOk(){
    this.isOkLoading = true;
    setTimeout(() => {
      this.isVisible = false;
      this.isOkLoading = false;
    }, 3000);
  }

  submitForm(){
    console.log(this.validateForm.value)
  }

  pageIndexChange(e:any){
    console.log(e)
    this.page = e
  }
  pageSizeChange(e:any){
    console.log(e)
    this.pageSize = e
  }


}
