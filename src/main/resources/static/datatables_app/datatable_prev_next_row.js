// This is from https://github.com/edouardl/datatable-prev-next-row, commit
// f30c6d4

/**
 *  Inspire by the fnGetAdjacentTr plugin and based on the 1.10 API,
 *  this plugin permit to get the next row of the currently selected
 *  
 *  The result could be chained to the DataTable Rows API (if next row exists)
 *
 *  @version 1.0
 *  @name row().next()
 *  @summary Get the next row of the currently selected
 *  @author [Edouard Labre](http://www.edouardlabre.com)
 *
 *  @param {void} a row must be selected
 *  @returns {DataTables.Api.Rows} DataTables Rows API instance (OR null if no result)
 *
 *  @example
 *    var table = $('#example').DataTable();
 *    table.row( current_row_selector ).next(); // Similar to table.row( next_row_selector );
 *    
 */
$.fn.dataTable.Api.register('row().next()', function() {
    // Current row position
    var nrp = this.table().rows()[0].indexOf( this.index() ) + 1;
	// Exists ?
    if( nrp < 0 ) {
        return null;
    }
    // Next row index by position
    var nri = this.table().rows()[0][ nrp ];
    // Return next row by its index
    return this.table().row( nri );
});

/**
 *  Inspire by the fnGetAdjacentTr plugin and based on the 1.10 API,
 *  this plugin permit to get the previous row of the currently selected
 *  
 *  The result could be chained to the DataTable Rows API (if previous row exists)
 *
 *  @version 1.0
 *  @name row().prev()
 *  @summary Get the previous row of the currently selected
 *  @author [Edouard Labre](http://www.edouardlabre.com)
 *
 *  @param {void} a row must be selected
 *  @returns {DataTables.Api.Rows} DataTables Rows API instance (OR null if no result)
 *
 *  @example
 *    var table = $('#example').DataTable();
 *    table.row( current_row_selector ).prev(); // Similar to table.row( prev_row_selector );
 *    
 */
$.fn.dataTable.Api.register('row().prev()', function() {
    // Next row position
    var prp = this.table().rows()[0].indexOf( this.index() ) - 1;
	// Exists ?
    if( prp < 0 ) {
        return null;
    }
    // Previous row index by position
    var pri = ( this.table().rows()[0][ prp ] );
    // Return previous row by its index
    return this.table().row( pri );
});
