import { Injectable } from '@angular/core';
import { AES, enc, mode, pad } from 'crypto-js';

@Injectable({
  providedIn:'root'
})
export class CryptoService {
  private key: string = 'cccv613bbcabdcbaf322ff6fb37204bb';
  constructor() {
  }
  /**
   * AES加密
   */
  encryptByEnAES(data: string): string {
    let Key = enc.Utf8.parse(this.key);
    let tmpAES = AES.encrypt(enc.Utf8.parse(data), Key, {
      mode: mode.ECB,
      padding: pad.Pkcs7
    });
    return tmpAES.toString();
  }

  /**
   * AES解密
   */
  encryptByDeAES(data: string): string {
    let Key = enc.Utf8.parse(this.key);
    let tmpDeAES = AES.decrypt(data, Key, {
      mode: mode.ECB,
      padding: pad.Pkcs7
    });
    return tmpDeAES.toString(enc.Utf8);
  }
}
