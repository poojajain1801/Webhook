/** COPYRIGHT: Comviva Technologies Pvt. Ltd. 
 * This software is the sole property of Comviva and is protected 
 * by copyright law and international treaty provisions. Unauthorized 
 * reproduction or redistribution of this program, or any portion of 
 * it may result in severe civil and criminal penalties and will be 
 * prosecuted to the maximum extent possible under the law. 
 * Comviva reserves all rights not expressly granted. You may not 
 * reverse engineer, decompile, or disassemble the software, except 
 * and only to the extent that such activity is expressly permitted 
 * by applicable law notwithstanding this limitation. 
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY 
 * KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A 
 * PARTICULAR PURPOSE. YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY 
 * AND THE USE OF THIS SOFTWARE. Comviva SHALL NOT BE LIABLE FOR 
 * ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE OF OR INABILITY TO 
 * USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 **/
package com.comviva.mfs.hce.appserver.exception;


/**
 * Created by girish.desai on 4/27/2016.
 * class to throw actionRelated exceptions
 *
 */
public class HCEActionException extends RuntimeException {

    private final String messageCode;



    /**
     * method to get the messageCode
     * @return messageCode string
     */
    public String getMessageCode() {
        return messageCode;
    }

    /**
     * constructor of the class
     * @param messageCode messageCode
     */
    public HCEActionException(String messageCode) {
        super(messageCode);
        this.messageCode = messageCode ;
    }

}
