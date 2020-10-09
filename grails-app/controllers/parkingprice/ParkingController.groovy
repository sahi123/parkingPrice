package parkingprice

import grails.converters.JSON
import grails.rest.RestfulController
import org.springframework.http.HttpStatus

import java.time.LocalDateTime

class ParkingController extends RestfulController {

    static allowedMethods = ['calParkingLotPrice': 'GET', 'parkingPaidByVehicle': 'GET', 'parkingVehiclesList': 'GET']

    def parkingPriceService

    ParkingController() {
        super(VehicleParkingDao)
    }

    /* REST API to calculate the parking price of the vehicle based on the entry and the exit time of the vehicle.
    I/P PARAMS:
    String entryTime which contains the entrytime of vehicle in dd-MMM-YYYY h:mm:ss format.
    String exitTime which contains the exitTime of vehicle in dd-MMM-YYYY h:mm:ss format.

    Response:
    status-> HttpStatus of request
    parkingPrice -> Parking price calculated based on the entry time and exit time.
    */

    def calParkingLotPrice(String entryTime, String exitTime) {
        Map result = [status: HttpStatus.BAD_REQUEST, parkingPrice: 0, message: "Success"]
        try {
            if (entryTime && exitTime) {
                LocalDateTime entryDate = parkingPriceService.dateStringToLocalDateTime(entryTime)
                LocalDateTime exitDate = parkingPriceService.dateStringToLocalDateTime(exitTime)
                result.parkingPrice = parkingPriceService.calculatePrice(entryDate, exitDate)
                result.status = HttpStatus.OK
                result.message = result.parkingPrice ? "Parking hours greater than 24" : "Success"
            }
        } catch (Exception ex) {
            log.error(ex.printStackTrace())
            result.status = HttpStatus.INTERNAL_SERVER_ERROR
            result.message = "Parking price cannot be calculated for entryTime: ${entryTime} and exitTime: ${exitTime}. Please contact the Administrator"
        }
        render(result as JSON)
    }

    /* REST API to return the list of parkings that the vehicle has done based on the passed vehicle id.
    I/P PARAMS:
    Long vehicleId

    Response:
    status-> HttpStatus of request
    data -> List of the VehicleParking based on the passed vehicleId.
    message -> Info message
    */

    def parkingByVehicle(Long vehicleId) {
        Map result = [status: HttpStatus.BAD_REQUEST, data: [], message: "Success"]
        try {
            VehicleDao vehicleDao = vehicleId ? VehicleDao.findById(vehicleId) : null
            if (vehicleDao) {
                result.data = parkingPriceService.parkingByVehicle(vehicleDao)
                result.status = HttpStatus.OK
            }
        } catch (Exception ex) {
            log.error(ex.printStackTrace())
            result.status = HttpStatus.INTERNAL_SERVER_ERROR
            result.message = "Could not show the vehicle parking list at this moment. Please contact the Administrator"
        }
        render(result as JSON)
    }

    /* REST API to return the list of Vehical parkings.
   I/P PARAMS:
   NONE

   Response:
   status-> HttpStatus of request
   data -> List of the VehicleParking.
   message -> info message
   */

    def parkingVehiclesList() {
        Map result = [status: HttpStatus.BAD_REQUEST, data: [], message: "Success"]
        try {
            result.data = parkingPriceService.parkingVehiclesList()
            result.status = HttpStatus.OK
        } catch (Exception ex) {
            log.error(ex.printStackTrace())
            result.status = HttpStatus.INTERNAL_SERVER_ERROR
            result.message = "Could not show the vehicle parking list a this moment. Please contact the Administrator"
        }
        render(result as JSON)
    }
}
