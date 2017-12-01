#Makefile needs to be improved for better OS detection
#(e.g. Linux x86 vs amd64, etc
ifeq ($(OS),Windows_NT)
    ifeq ($(PROCESSOR_ARCHITECTURE),AMD64)
        LD_FLAGS := -Llib/windows/amd64
    endif
    ifeq ($(PROCESSOR_ARCHITECTURE),x86)
        LD_FLAGS := -Llib/windows/x86
    endif
else
    UNAME_S := $(shell uname -s)
    ifeq ($(UNAME_S),Linux)
        LD_FLAGS := -Llib/linux
    endif
    ifeq ($(UNAME_S),Darwin)
        LD_FLAGS := -Llib/macos
    endif
endif

CXX		    := g++
CC		    := g++
LD        := g++
CC_FLAGS  := -Wall -std=c++11 -ggdb -g -pthread -fPIC -frtti -pipe #-m32
LD_FLAGS  += -Llib -lcryptopp -lboost_system -lgtest #-m32
#CFLAGS += -I$(MF_WHITEBOX_SDKDIR)/code -m32 -g

ODIR      := ./obj

INCLUDE   := -I./../src/ -I./../unit_test -I./../ -I./../gtest-1.7.0/include/

UTILS_H   := ../src/utils/byte_array.h \
						 ../src/utils/crypto_factory.h \
						 ../src/utils/emvco/apdu.h \
						 ../src/utils/emvco/ccc_response_apdu.h \
						 ../src/utils/emvco/command_apdu.h \
						 ../src/utils/emvco/compute_cc_apdu.h \
						 ../src/utils/emvco/generate_ac_apdu.h \
						 ../src/utils/emvco/generate_ac_response_apdu.h \
						 ../src/utils/emvco/gpo_apdu.h \
						 ../src/utils/emvco/read_record_apdu.h \
						 ../src/utils/emvco/response_apdu.h \
						 ../src/utils/emvco/select_apdu.h \
						 ../src/utils/mcbp_core_exception.h \
						 ../src/utils/random_transaction_keys.h \
						 ../src/utils/rsa_certificate.h \
						 ../src/utils/transaction_keys.h \
						 ../src/utils/utilities.h \
						 ../src/utils/pin_validator.h \
						 ../src/log/log.h \
						 ../src/wrappers/unit_test_wrapper.h \
						 ../src/wrappers/card_profile_data.h \
						 ../src/wrappers/keys_data.h

UTILS_SRC := ../src/utils/byte_array.cpp \
						 ../src/utils/crypto_factory.cpp \
						 ../src/utils/emvco/apdu.cpp \
						 ../src/utils/emvco/ccc_response_apdu.cpp \
						 ../src/utils/emvco/command_apdu.cpp \
						 ../src/utils/emvco/compute_cc_apdu.cpp \
						 ../src/utils/emvco/generate_ac_apdu.cpp \
						 ../src/utils/emvco/generate_ac_response_apdu.cpp \
						 ../src/utils/emvco/gpo_apdu.cpp \
						 ../src/utils/emvco/read_record_apdu.cpp \
						 ../src/utils/emvco/response_apdu.cpp \
						 ../src/utils/emvco/select_apdu.cpp \
						 ../src/utils/mcbp_core_exception.cpp \
						 ../src/utils/random_transaction_keys.cpp \
						 ../src/utils/rsa_certificate.cpp \
						 ../src/utils/transaction_keys.cpp \
						 ../src/utils/utilities.cpp \
						 ../src/utils/pin_validator.cpp \
						 ../src/log/log.cpp \
						 ../src/wrappers/unit_test_wrapper.cpp

UTILS_OBJ := $(notdir $(UTILS_SRC:.cpp=.o))

CORE_SRC  := ../src/core/mcbp_card.cpp \
	           ../src/core/card_utilities.cpp \
						 ../src/core/mcm/alternate_contactless_payment_data.cpp \
						 ../src/core/mcm/card_record.cpp \
						 ../src/core/mcm/card_risk_management_data.cpp \
						 ../src/core/mcm/contactless_context.cpp \
						 ../src/core/mcm/contactless_payment_data.cpp \
						 ../src/core/mcm/contactless_transaction_context.cpp \
						 ../src/core/mcm/cryptogram_input.cpp \
						 ../src/core/mcm/cryptogram_output.cpp \
						 ../src/core/mcm/mcm_card_profile.cpp \
						 ../src/core/mcm/mcm_lite_services.cpp \
						 ../src/core/mcm/remote_payment_data.cpp \
						 ../src/core/mcm/transaction_output.cpp \
						 ../src/core/mobile_kernel/dsrp_input_data.cpp \
						 ../src/core/mobile_kernel/dsrp_output_data.cpp \
						 ../src/core/mobile_kernel/mobile_kernel.cpp

#MF_SRC :=	../src/core/mpp/my3des-wb.c \
#  					../src/core/mpp/myGenAC-wb.c

CORE_H    := ../src/core/constants.h \
						 ../src/core/mcbp_card.h \
						 ../src/core/card_utilities.h \
						 ../src/core/mcm/alternate_contactless_payment_data.h \
						 ../src/core/mcm/card_record.h \
             ../src/core/mcm/card_risk_management_data.h \
						 ../src/core/mcm/contactless_context.h \
						 ../src/core/mcm/contactless_payment_data.h \
						 ../src/core/mcm/contactless_transaction_context.h \
						 ../src/core/mcm/cryptogram_input.h \
						 ../src/core/mcm/cryptogram_output.h \
						 ../src/core/mcm/mcm_card_profile.h \
						 ../src/core/mcm/mcm_lite_services.h \
						 ../src/core/mcm/remote_payment_data.h \
						 ../src/core/mcm/transaction_output.h \
						 ../src/core/mobile_kernel/dsrp_input_data.h \
						 ../src/core/mobile_kernel/dsrp_output_data.h \
						 ../src/core/mobile_kernel/mobile_kernel.h


CORE_OBJ  := $(notdir $(CORE_SRC:.cpp=.o))

CRYPTO_SERVICE_SRC	:= ../src/cryptoservice/crypto_functions.cpp \
                       ../src/cryptoservice/mobile_keys.cpp

CRYPTO_SERVICE_H	  := ../src/cryptoservice/crypto_functions.h \
                       ../src/cryptoservice/mobile_keys.h

CRYPTO_SERVICE_OBJ  := $(notdir $(CRYPTO_SERVICE_SRC:.cpp=.o))

UNIT_SRC  := ../unit_test/unit_test_utilities.cpp \
             ../unit_test/test_data.cpp \
						 ../unit_test/mcbp_card_test.cpp \
					   ../unit_test/crypto_factory_test.cpp \
					   ../unit_test/crypto_functions_test.cpp

UNIT_H    := ../unit_test/unit_test_utilities.h \
             ../unit_test/test_data.h \
						 ../unit_test/mcbp_card_test.h \
						 ../unit_test/crypto_factory_test.h \
						 ../unit_test/crypto_functions_test.h

UNIT_OBJ  := $(notdir $(UNIT_SRC:.cpp=.o))

#MF_OBJ :=  $(MF_SRC:.c=.o)

mcbp      : mcbp.o # $(MF_OBJ)
	$(CXX) $(CFLAGS) $(CC_FLAGS) $(INCLUDE) ../unit_test/main.cpp -o mcbp_unit_test $(CORE_OBJ) $(UTILS_OBJ) $(CRYPTO_SERVICE_OBJ) $(UNIT_OBJ) $(MF_OBJ) $(LD_FLAGS)

mcbp.o    : $(UTILS_H) $(UTILS_SRC) $(CORE_H) $(CORE_SRC) $(CRYPTO_SERVICE_H) $(CRYPTO_SERVICE_SRC) $(UNIT_H) $(UNIT_SRC)
	$(CXX) $(CFLAGS) $(CC_FLAGS) $(INCLUDE) -c $(UTILS_SRC) $(CORE_SRC) $(UNIT_SRC) $(CRYPTO_SERVICE_SRC) #../lib/macos/libgtest.a

clean:
	rm -rf *.o $(MF_OBJ)

#$(UNIT_OBJ) $(UNIT_SRC)

debug:
	@echo "Using $(LD_FLAGS)"