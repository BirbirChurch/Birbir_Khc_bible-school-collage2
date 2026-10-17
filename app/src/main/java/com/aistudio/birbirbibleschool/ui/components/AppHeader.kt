package com.aistudio.birbirbibleschool.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Church
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.birbirbibleschool.model.CurrentUser
import com.aistudio.birbirbibleschool.model.UserRole
import com.aistudio.birbirbibleschool.ui.theme.Amber400
import com.aistudio.birbirbibleschool.ui.theme.Emerald800
import com.aistudio.birbirbibleschool.ui.theme.Emerald900
import com.aistudio.birbirbibleschool.ui.theme.Emerald950
import com.aistudio.birbirbibleschool.ui.theme.Red600

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppHeader(
    currentUser: CurrentUser?,
    systemLocked: Boolean,
    currentYear: String,
    currentSemester: String,
    adminActiveView: UserRole,
    onAdminChangeView: (UserRole) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Emerald950,
        shadowElevation = 8.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Top Row: Logo, School Name, Current User and Logout
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Emerald800),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Church,
                            contentDescription = "የቤተ ክርስቲያን አርማ",
                            tint = Amber400,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "የብርብር ከተማ ቃለ ሕይወት ቤተ ክርስቲያን",
                            color = Amber400,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            maxLines = 1
                        )
                        Text(
                            text = "መጽሐፍ ቅዱስ ትምህርት ቤት ሲስተም",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                }

                if (currentUser != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // User Role Badge
                        Surface(
                            color = Emerald800,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when (currentUser.role) {
                                        UserRole.ADMIN -> Icons.Default.Shield
                                        UserRole.STUDENT -> Icons.Default.School
                                        else -> Icons.Default.Person
                                    },
                                    contentDescription = null,
                                    tint = Amber400,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = currentUser.name,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                            }
                        }

                        IconButton(
                            onClick = onLogout,
                            modifier = Modifier.testTag("logout_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "ውጣ (Logout)",
                                tint = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // Status bar info: Calendar Year/Sem & Maintenance Banner
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = Emerald900,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "📅 ዘመን: $currentYear ዓ.ም | ሴሚስተር: $currentSemester",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                if (systemLocked) {
                    Surface(
                        color = Red600,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ጥገና ሁነታ (Locked)",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Admin View Switcher (Only visible to ADMIN to preview all roles seamlessly)
            if (currentUser?.role == UserRole.ADMIN) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "የእይታ መቀየሪያ (Admin Universal View Switcher):",
                    color = Amber400,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        UserRole.ADMIN to "ዋና አድሚን",
                        UserRole.DEAN to "ዲን",
                        UserRole.REGISTRAR to "ሪጅስተራል",
                        UserRole.TEACHER to "መምህራን",
                        UserRole.STUDENT to "ተማሪዎች"
                    ).forEach { (role, label) ->
                        val isSelected = adminActiveView == role
                        FilterChip(
                            selected = isSelected,
                            onClick = { onAdminChangeView(role) },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Amber400,
                                selectedLabelColor = Emerald950,
                                containerColor = Emerald900,
                                labelColor = Color.White
                            ),
                            modifier = Modifier.testTag("admin_view_${role.name.lowercase()}")
                        )
                    }
                }
            }
        }
    }
}
