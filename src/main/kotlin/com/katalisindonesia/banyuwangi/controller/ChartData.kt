package com.katalisindonesia.banyuwangi.controller

import io.swagger.v3.oas.annotations.media.Schema

data class ChartData<T>(
    @Schema(description = "List of series names")
    val seriesNames: List<String>,
    @Schema(description = "Series labels")
    val labels: List<T>,
    @Schema(description = "Data of the series")
    val data: Map<String, List<Long>>,

    @Schema(description = "UUIDs of the snapshots for each datum")
    val snapshotIds: Map<String, List<String>>,
)
