package com.sap.cloud.s4hana.examples.addressmgr

import com.google.gson.Gson
import com.sap.cloud.s4hana.examples.addressmgr.commands.GetAllBusinessPartnersCommand
import com.sap.cloud.s4hana.examples.addressmgr.commands.GetSingleBusinessPartnerByIdCommand
import com.sap.cloud.sdk.cloudplatform.logging.CloudLoggerFactory
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.DefaultBusinessPartnerService
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet("/api/business-partners")
class BusinessPartnerServlet : HttpServlet() {

    private val service = DefaultBusinessPartnerService()

    @Throws(ServletException::class, IOException::class)
    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        val id: String? = request.getParameter("id")

        val jsonResult: String = if (id == null) {
            logger.info("Retrieving all business partners")
            Gson().toJson(GetAllBusinessPartnersCommand(service).execute())
        } else {
            if (!id.isValidId()) {
                logger.warn("Invalid request to retrieve a business partner, id: $id.")
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid business partner ID '$id'. Business partner ID must not be empty or longer than 10 characters."
                )
                return
            }
            logger.info("Retrieving business partner with id $id")
            Gson().toJson(GetSingleBusinessPartnerByIdCommand(service, id).execute())
        }

        response.contentType = "application/json"
        response.writer.write(jsonResult)
    }

    private fun String.isValidId() = this.isNotEmpty() && this.length <= 10

    companion object {
        private const val serialVersionUID = 2L
        private val logger = CloudLoggerFactory.getLogger(BusinessPartnerServlet::class.java)
    }
}
