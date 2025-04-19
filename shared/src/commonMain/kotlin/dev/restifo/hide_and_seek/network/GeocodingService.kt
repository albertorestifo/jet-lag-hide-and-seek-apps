package dev.restifo.hide_and_seek.network

import dev.restifo.hide_and_seek.config.BuildConfig
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

/**
 * Service for geocoding-related operations.
 */
interface GeocodingService {
    /**
     * Search for locations matching the query.
     */
    suspend fun searchLocations(query: String, limit: Int = 10): List<LocationSearchResult>

    /**
     * Get the boundaries of a location by its ID.
     */
    suspend fun getLocationBoundaries(locationId: String): LocationBoundaries

    companion object {
        private var instance: GeocodingService? = null

        fun getInstance(): GeocodingService {
            if (instance == null) {
                instance = GeocodingServiceImpl()
            }
            return instance!!
        }
    }
}

/**
 * Implementation of the GeocodingService interface.
 */
class GeocodingServiceImpl : GeocodingService {
    private val apiClient = ApiClient.getInstance()

    /**
     * Search for locations matching the query.
     */
    override suspend fun searchLocations(query: String, limit: Int): List<LocationSearchResult> {
        val response: ApiResponse<List<LocationSearchResult>> = apiClient.httpClient.get {
            url("${BuildConfig.apiBaseUrl}/api/geocoding/autocomplete")
            parameter("query", query)
            parameter("limit", limit)
        }.body()

        return response.data
    }

    /**
     * Get the boundaries of a location by its ID.
     */
    override suspend fun getLocationBoundaries(locationId: String): LocationBoundaries {
        val response: ApiResponse<LocationBoundaries> = apiClient.httpClient.get {
            url("${BuildConfig.apiBaseUrl}/api/geocoding/boundaries/$locationId")
        }.body()

        return response.data
    }
}

/**
 * Generic API response wrapper.
 */
@Serializable
data class ApiResponse<T>(
    val data: T
)

/**
 * A location search result.
 */
@Serializable
data class LocationSearchResult(
    val id: String,
    val title: String,
    val subtitle: String,
    val type: String? = null,
    val osm_type: String? = null,
    val osm_id: String? = null,
    val coordinates: List<Double>? = null
)

/**
 * Boundaries of a location.
 */
@Serializable
data class LocationBoundaries(
    val name: String,
    val osm_id: String,
    val osm_type: String,
    val type: String? = null,
    val coordinates: List<Double>? = null,
    val boundaries: String? = null
)
