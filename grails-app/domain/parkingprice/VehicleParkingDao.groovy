package parkingprice

import java.time.LocalDateTime

class VehicleParkingDao {
    Long id
    VehicleDao vehicle
    Integer parkingPrice
    LocalDateTime entryTime
    LocalDateTime exitTime

    static constraints = {
        parkingPrice nullable: true
        entryTime nullable: true
        exitTime nullable: true
        vehicle nullable: false
    }
}
