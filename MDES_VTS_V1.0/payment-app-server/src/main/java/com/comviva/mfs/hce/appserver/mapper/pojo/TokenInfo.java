/*
 * COPYRIGHT(c) 2015: Comviva Technologies Pvt. Ltd.
 *
 * This software is the sole property of Comviva and is protected by copyright
 * law and international treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of it may result in severe
 * civil and criminal penalties and will be prosecuted to the maximum extent
 * possible under the law. Comviva reserves all rights not expressly granted.
 * You may not reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted by
 * applicable law notwithstanding this limitation.
 *
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY AND THE USE OF THIS SOFTWARE.
 * Comviva SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE
 * USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 * Created by Amgoth.madan on 1/8/2017.
 */
@Getter
@ToString
@NoArgsConstructor
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class TokenInfo {

    private String  tokenStatus;
    private String  tokenRequestorID;
    private String  tokenReferenceID;
    private String  last4;
    private ExpirationDate expirationDate;
    private String appPrgrmID;
    private String encTokenInfo;
    private String hceData;
    private String mst;
    private String seCardPerso;

   /* public TokenInfo(String tokenStatus,String tokenRequestorID,String tokenReferenceID,String last4,ExpirationDate expirationDate,String appPrgrmID,
                     String encTokenInfo,String hceData,String mst,String seCardPerso)
    {
        this.tokenStatus=tokenStatus;
        this.tokenRequestorID=tokenRequestorID;
        this.tokenReferenceID=tokenReferenceID;
        this.last4=last4;
        this.expirationDate=expirationDate;
        this.appPrgrmID=appPrgrmID;
        this.encTokenInfo=encTokenInfo;
        this.hceData=hceData;
        this.mst=mst;
        this.seCardPerso=seCardPerso;
    }*/
}
