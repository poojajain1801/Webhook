/*******************************************************************************
 * Copyright (c) 2015, MasterCard International Incorporated and/or its
 * affiliates. All rights reserved.
 *
 * The contents of this file may only be used subject to the MasterCard
 * Mobile Payment SDK for MCBP and/or MasterCard Mobile MPP UI SDK
 * Materials License.
 *
 * Please refer to the file LICENSE.TXT for full details.
 *
 * TO THE EXTENT PERMITTED BY LAW, THE SOFTWARE IS PROVIDED "AS IS", WITHOUT 
 * WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NON INFRINGEMENT. TO THE EXTENT PERMITTED BY LAW, IN NO EVENT SHALL
 * MASTERCARD OR ITS AFFILIATES BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
*******************************************************************************/


/**
 * The following coding guidelines have been followed
 * http://google-styleguide.googlecode.com/svn/trunk/cppguide.xml
 *
 * MCBP = MasterCard Cloud-Based Payments
 */

#include <core/mcm/contactless_transaction_context.h>

#ifndef SRC_CORE_MCM_MCM_LITE_LISTENER_H_  // NOLINT
#define SRC_CORE_MCM_MCM_LITE_LISTENER_H_  // NOLINT

namespace mcbp_core {

/**
 * The McmLite listener interface for receiving events from the Mcm Lite
 * The class that is interested in processing these
 * events implements this interface, and the object created
 * with that class is registered when the Mcm Lite object is created.
 * When the Mcm Lite event occurs, that object's appropriate method is invoked.
 * The listener will be receive the different events and related transaction 
 * context. It is responsability of the listener to parse the message and
 * notify other relevant modules accordingly
 */
class McmLiteListener {
 public:
    /**
     * Receives an event from the Mcm Lite.
     * The event contains the entire ContactlessTransactionContext which can
     * be used accordingly by the application logic (e.g. store logs, update the
     * User Interface, etc
     * @param context The contactless transaction context
     */
    virtual void on_event(const ContactlessTransactionContext& context) = 0;

    /**
     * Virtual denscructor
     */
    virtual ~McmLiteListener() { }
};

}  // namespace mcbp_core

#endif  // defined(SRC_CORE_MPP_MPP_MCBP_LISTENER_H_)  // NOLINT
