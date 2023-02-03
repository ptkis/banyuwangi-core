package com.katalisindonesia.banyuwangi.consumer

import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.DefaultTransactionDefinition

fun txDef(
    name: String,
    isolationLevel: Int = TransactionDefinition.ISOLATION_REPEATABLE_READ,
    propagation: Int = TransactionDefinition.PROPAGATION_REQUIRED,
): TransactionDefinition {
    val def = DefaultTransactionDefinition()

    def.setName(name)
    def.isolationLevel = isolationLevel
    def.propagationBehavior = propagation

    return def
}
