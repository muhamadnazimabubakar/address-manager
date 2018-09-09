package com.sap.cloud.s4hana.examples.addressmgr.commands

import com.sap.cloud.sdk.cloudplatform.logging.CloudLoggerFactory
import com.sap.cloud.sdk.frameworks.hystrix.HystrixUtil
import com.sap.cloud.sdk.s4hana.connectivity.ErpCommand
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartner
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartnerAddress
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.BusinessPartnerService
import org.slf4j.Logger

class GetSingleBusinessPartnerByIdCommand(private val service: BusinessPartnerService, private val id: String) : ErpCommand<BusinessPartner>(HystrixUtil.getDefaultErpCommandSetter(
        GetSingleBusinessPartnerByIdCommand::class.java,
        HystrixUtil.getDefaultErpCommandProperties().withExecutionTimeoutInMilliseconds(10000))) {

    @Throws(Exception::class)
    override fun run(): BusinessPartner {
        return service
                .getBusinessPartnerByKey(id)
                .select(BusinessPartner.BUSINESS_PARTNER,
                        BusinessPartner.LAST_NAME,
                        BusinessPartner.FIRST_NAME,
                        BusinessPartner.IS_MALE,
                        BusinessPartner.IS_FEMALE,
                        BusinessPartner.CREATION_DATE,
                        BusinessPartner.TO_BUSINESS_PARTNER_ADDRESS.select(
                                BusinessPartnerAddress.BUSINESS_PARTNER,
                                BusinessPartnerAddress.ADDRESS_ID,
                                BusinessPartnerAddress.COUNTRY,
                                BusinessPartnerAddress.POSTAL_CODE,
                                BusinessPartnerAddress.CITY_NAME,
                                BusinessPartnerAddress.STREET_NAME,
                                BusinessPartnerAddress.HOUSE_NUMBER))
                .execute()
    }

    override fun getFallback(): BusinessPartner {
        logger.warn("Fallback called because of exception:", executionException)
        return BusinessPartner.builder().businessPartner(id).build()
    }

    companion object {
        private val logger = CloudLoggerFactory.getLogger(GetSingleBusinessPartnerByIdCommand::class.java)
    }
}
