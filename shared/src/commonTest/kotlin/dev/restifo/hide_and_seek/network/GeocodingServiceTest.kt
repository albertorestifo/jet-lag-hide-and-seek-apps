package dev.restifo.hide_and_seek.network

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GeocodingServiceTest {
    
    @Test
    fun testLocationSearchResultStructure() {
        // Given
        val id = "way:123456"
        val title = "Madrid"
        val subtitle = "City"
        val type = "city"
        val osmType = "way"
        val osmId = "123456"
        val coordinates = listOf(-3.7038, 40.4168)
        
        // When
        val result = LocationSearchResult(
            id = id,
            title = title,
            subtitle = subtitle,
            type = type,
            osm_type = osmType,
            osm_id = osmId,
            coordinates = coordinates
        )
        
        // Then
        assertEquals(id, result.id)
        assertEquals(title, result.title)
        assertEquals(subtitle, result.subtitle)
        assertEquals(type, result.type)
        assertEquals(osmType, result.osm_type)
        assertEquals(osmId, result.osm_id)
        assertEquals(coordinates, result.coordinates)
    }
    
    @Test
    fun testLocationBoundariesStructure() {
        // Given
        val name = "Madrid"
        val osmId = "123456"
        val osmType = "way"
        val type = "city"
        val coordinates = listOf(-3.7038, 40.4168)
        
        // When
        val boundaries = LocationBoundaries(
            name = name,
            osm_id = osmId,
            osm_type = osmType,
            type = type,
            coordinates = coordinates,
            boundaries = null
        )
        
        // Then
        assertEquals(name, boundaries.name)
        assertEquals(osmId, boundaries.osm_id)
        assertEquals(osmType, boundaries.osm_type)
        assertEquals(type, boundaries.type)
        assertEquals(coordinates, boundaries.coordinates)
    }
    
    @Test
    fun testApiResponseStructure() {
        // Given
        val data = LocationSearchResult(
            id = "way:123456",
            title = "Madrid",
            subtitle = "City"
        )
        
        // When
        val response = ApiResponse(data = data)
        
        // Then
        assertNotNull(response.data)
        assertEquals("Madrid", response.data.title)
    }
}
